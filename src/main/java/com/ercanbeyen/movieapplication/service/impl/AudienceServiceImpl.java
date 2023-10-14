package com.ercanbeyen.movieapplication.service.impl;

import com.ercanbeyen.movieapplication.constant.enums.RoleName;
import com.ercanbeyen.movieapplication.constant.message.LogMessages;
import com.ercanbeyen.movieapplication.constant.message.ResponseMessages;
import com.ercanbeyen.movieapplication.constant.names.ResourceNames;
import com.ercanbeyen.movieapplication.dto.AudienceDto;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
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
        Role role = roleService.findRoleByRoleName(RoleName.USER);
        Set<Role> roleSet = Set.of(role);

        Audience newAudience = Audience.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .roles(roleSet)
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
    public AudienceDto getAudience(Integer id) {
        Audience audienceInDb = findAudienceById(id);
        return audienceDtoConverter.convert(audienceInDb);
    }

    @Override
    public List<AudienceDto> getAudiences() {
        return audienceRepository.findAll()
                .stream()
                .map(audienceDtoConverter::convert)
                .toList();
    }

    @Override
    public AudienceDto updateAudience(Integer id, UpdateAudienceRequest request, UserDetails userDetails) {
        Audience audienceInDb = findAudienceById(id);

        audienceInDb.setUsername(request.getUsername());
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

    @Override
    public void deleteAudience(Integer id, UserDetails userDetails) {
        audienceRepository.deleteById(id);
        log.info(LogMessages.DELETED, ResourceNames.AUDIENCE);
    }

    @Override
    public String updateRolesOfAudience(Integer id, Set<RoleName> roleNames, UserDetails userDetails) {
        if (!roleNames.contains(RoleName.USER)) {
            throw new ResourceConflictException(ResourceNames.ROLE + " " + RoleName.USER + " is mandatory");
        }

        Audience audienceInDb = findAudienceById(id);

        if (audienceInDb.getUsername().equals(userDetails.getUsername()) && !roleNames.contains(RoleName.ADMIN)) {
            throw new ResourceConflictException("You cannot remove your " + RoleName.ADMIN + " " + ResourceNames.ROLE.toLowerCase());
        }

        Set<Role> roleSet = roleNames.stream()
                .map(roleService::findRoleByRoleName)
                .collect(Collectors.toSet());

        audienceInDb.setRoles(roleSet);
        log.info(LogMessages.FIELDS_SET);

        audienceRepository.save(audienceInDb);
        log.info(LogMessages.SAVED, ResourceNames.AUDIENCE);

        return ResponseMessages.SUCCESS;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Audience audience = audienceRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, ResourceNames.AUDIENCE)));

        return new User(username, audience.getPassword(), audience.getAuthorities());
    }

    public Audience findAudienceById(Integer id) {
        Optional<Audience> optionalAudience = audienceRepository.findById(id);

        if (optionalAudience.isEmpty()) {
            log.error(LogMessages.RESOURCE_NOT_FOUND, ResourceNames.AUDIENCE, id);
            throw new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, ResourceNames.AUDIENCE));
        }

        log.info(LogMessages.RESOURCE_FOUND, ResourceNames.AUDIENCE, id);
        return optionalAudience.get();
    }
}
