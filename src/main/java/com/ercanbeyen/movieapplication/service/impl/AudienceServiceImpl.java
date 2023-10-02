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

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AudienceServiceImpl implements AudienceService, UserDetailsService {
    private final AudienceRepository audienceRepository;
    private final AudienceDtoConverter audienceDtoConverter;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    @Override
    public AudienceDto createAudience(RegistrationRequest request) {
        log.info(LogMessages.STARTED, "createAudience");
        Role role = roleService.findRoleByRoleName(RoleName.USER);
        Set<Role> roleSet = Set.of(role);

        Audience newAudience = Audience.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .roles(roleSet)
                .name(request.name())
                .surname(request.surname())
                .nationality(request.nationality())
                .birthYear(request.birthYear())
                .biography(request.biography())
                .build();

        Audience savedAudience = audienceRepository.save(newAudience);
        log.info(LogMessages.SAVED, ResourceNames.AUDIENCE);

        return audienceDtoConverter.convert(savedAudience);
    }

    @Override
    public AudienceDto getAudience(Integer id) {
        log.info(LogMessages.STARTED, "getAudience");
        Audience audienceInDb = audienceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, ResourceNames.AUDIENCE, id)));
        return audienceDtoConverter.convert(audienceInDb);
    }

    @Override
    public AudienceDto updateAudience(Integer id, UpdateAudienceRequest request) {
        log.info(LogMessages.STARTED, "updateAudience");
        Audience audienceInDb = audienceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, ResourceNames.AUDIENCE, id)));
        log.info(LogMessages.FETCHED, ResourceNames.AUDIENCE);

        audienceInDb.setName(request.getName());
        audienceInDb.setSurname(request.getSurname());
        audienceInDb.setNationality(request.getNationality());
        audienceInDb.setBirthYear(request.getBirthYear());
        audienceInDb.setBiography(request.getBiography());
        log.info(LogMessages.FIELDS_SET);

        Audience savedAudience = audienceRepository.save(audienceInDb);
        log.info(LogMessages.SAVED, ResourceNames.AUDIENCE);

        return audienceDtoConverter.convert(savedAudience);
    }

    @Override
    public void deleteAudience(Integer id) {
        log.info(LogMessages.STARTED, "deleteAudience");
        boolean audienceExists = audienceRepository.existsById(id);

        if (!audienceExists) {
            throw new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, ResourceNames.AUDIENCE, id));
        }

        log.info(LogMessages.EXISTS, ResourceNames.AUDIENCE);
        audienceRepository.deleteById(id);
        log.info(LogMessages.DELETED, ResourceNames.AUDIENCE);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Audience audience = audienceRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, ResourceNames.AUDIENCE, username)));

        return new User(username, audience.getPassword(), audience.getAuthorities());
    }
}
