package com.ercanbeyen.movieapplication.service;

import com.ercanbeyen.movieapplication.dto.AudienceDto;
import com.ercanbeyen.movieapplication.dto.PageDto;
import com.ercanbeyen.movieapplication.dto.request.auth.RegistrationRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateAudienceRequest;
import com.ercanbeyen.movieapplication.entity.Audience;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;

public interface AudienceService {
    void createAudience(RegistrationRequest request);
    PageDto<Audience, AudienceDto> getAudiences(Pageable pageable);
    AudienceDto getAudience(Integer id);
    AudienceDto getAudience(String username);
    AudienceDto updateAudience(Integer id, UpdateAudienceRequest request, UserDetails userDetails);
    void deleteAudience(Integer id, UserDetails userDetails);
    String updateRolesOfAudience(Integer id, Set<String> roleNames, UserDetails userDetails);
    Audience findAudienceById(Integer id);
    Audience findAudienceByUsername(String username);
}
