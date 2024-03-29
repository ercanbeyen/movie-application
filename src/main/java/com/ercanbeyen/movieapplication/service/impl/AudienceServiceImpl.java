package com.ercanbeyen.movieapplication.service.impl;

import com.ercanbeyen.movieapplication.constant.message.LogMessages;
import com.ercanbeyen.movieapplication.constant.message.ResponseMessages;
import com.ercanbeyen.movieapplication.constant.names.ResourceNames;
import com.ercanbeyen.movieapplication.constant.names.RoleNames;
import com.ercanbeyen.movieapplication.dto.AudienceDto;
import com.ercanbeyen.movieapplication.dto.PageDto;
import com.ercanbeyen.movieapplication.dto.converter.AudienceDtoConverter;
import com.ercanbeyen.movieapplication.dto.request.auth.RegistrationRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateAudienceRequest;
import com.ercanbeyen.movieapplication.entity.Audience;
import com.ercanbeyen.movieapplication.entity.Role;
import com.ercanbeyen.movieapplication.exception.ResourceConflictException;
import com.ercanbeyen.movieapplication.exception.ResourceNotFoundException;
import com.ercanbeyen.movieapplication.repository.AudienceRepository;
import com.ercanbeyen.movieapplication.service.AudienceService;
import com.ercanbeyen.movieapplication.service.RoleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AudienceServiceImpl implements AudienceService, UserDetailsService {
    private final AudienceRepository audienceRepository;
    private final AudienceDtoConverter audienceDtoConverter;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    @Override
    public void createAudience(RegistrationRequest request) {
        CompletableFuture<Role> roleFuture = roleService.findRoleAsync(RoleNames.USER);

        Audience newAudience = Audience.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .roles(Set.of(roleFuture.join()))
                .name(request.name())
                .surname(request.surname())
                .nationality(request.nationality())
                .birthDate(request.birthDate())
                .biography(request.biography())
                .build();

        audienceRepository.save(newAudience);
        log.info(LogMessages.SAVED, ResourceNames.AUDIENCE);
    }

    @Override
    public PageDto<Audience, AudienceDto> getAudiences(Pageable pageable) {
        Page<Audience> audiencePage = audienceRepository.findAll(pageable);
        log.info(LogMessages.FETCHED_ALL, ResourceNames.AUDIENCE);

        List<AudienceDto> audienceDtoList = audiencePage.stream()
                .map(audienceDtoConverter::convert)
                .toList();

        return new PageDto<>(audiencePage, audienceDtoList);
    }

    @Override
    public AudienceDto getAudience(Integer id) {
        return audienceRepository.findAudienceDtoById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, ResourceNames.AUDIENCE)));
    }

    @Override
    public AudienceDto getAudience(String username) {
        return audienceRepository.findAudienceDtoByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, ResourceNames.AUDIENCE)));
    }

    @Override
    public AudienceDto updateAudience(Integer id, UpdateAudienceRequest request, UserDetails userDetails) {
        Audience audienceInDb = findAudienceById(id);

        audienceInDb.setPassword(passwordEncoder.encode(request.getPassword()));
        audienceInDb.setName(request.getName());
        audienceInDb.setSurname(request.getSurname());
        audienceInDb.setNationality(request.getNationality());
        audienceInDb.setBirthDate(request.getBirthDate());
        audienceInDb.setBiography(request.getBiography());
        log.info(LogMessages.FIELDS_SET);

        Audience savedAudience = audienceRepository.save(audienceInDb);
        log.info(LogMessages.SAVED, ResourceNames.AUDIENCE);

        return audienceDtoConverter.convert(savedAudience);
    }

    @Transactional
    @Override
    public void deleteAudience(Integer id, UserDetails userDetails) {
        audienceRepository.findById(id)
                .ifPresentOrElse(audience -> {
                    audience.getRatings().forEach(rating -> rating.setAudience(null));
                    audience.setRatings(null);
                    audienceRepository.delete(audience);
                    log.info(LogMessages.DELETED, ResourceNames.AUDIENCE);
                    }, () -> {
                    throw new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, ResourceNames.AUDIENCE));
                });
    }

    @Override
    public String updateRolesOfAudience(Integer id, Set<String> roleNames, UserDetails userDetails) {
        if (!roleNames.contains(RoleNames.USER)) {
            throw new ResourceConflictException(ResourceNames.ROLE + " " + RoleNames.USER + " is mandatory");
        }

        Audience audienceInDb = findAudienceById(id);

        if (audienceInDb.getUsername().equals(userDetails.getUsername()) && !roleNames.contains(RoleNames.ADMIN)) {
            throw new ResourceConflictException("You cannot remove your " + RoleNames.ADMIN + " " + ResourceNames.ROLE.toLowerCase());
        }

        Set<Role> roleSet = roleNames.stream()
                .map(roleService::findRole)
                .collect(Collectors.toSet());

        audienceInDb.setRoles(roleSet);
        log.info(LogMessages.FIELDS_SET);

        audienceRepository.save(audienceInDb);
        log.info(LogMessages.SAVED, ResourceNames.AUDIENCE);

        return ResponseMessages.SUCCESS;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Audience audience = findAudienceByUsername(username);
        return new User(username, audience.getPassword(), audience.getAuthorities());
    }

    @Override
    public Audience findAudience(Integer id) {
        return findAudienceById(id);
    }

    @Async
    @Override
    public CompletableFuture<Audience> findAudienceAsync(String username) {
        return audienceRepository.findByUsername(username);
    }

    private Audience findAudienceById(Integer id) {
        return audienceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, ResourceNames.AUDIENCE)));
    }

    private Audience findAudienceByUsername(String username) {
        return audienceRepository.findAudience(username)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, ResourceNames.AUDIENCE)));
    }
}
