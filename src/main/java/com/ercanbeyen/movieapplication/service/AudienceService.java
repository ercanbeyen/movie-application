package com.ercanbeyen.movieapplication.service;

import com.ercanbeyen.movieapplication.constant.enums.RoleName;
import com.ercanbeyen.movieapplication.dto.AudienceDto;
import com.ercanbeyen.movieapplication.dto.request.auth.RegistrationRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateAudienceRequest;
import com.ercanbeyen.movieapplication.entity.Audience;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Set;

public interface AudienceService {
    void createAudience(RegistrationRequest request);
    AudienceDto getAudience(Integer id);
    List<AudienceDto> getAudiences();
    AudienceDto updateAudience(Integer id, UpdateAudienceRequest request, UserDetails userDetails);
    void deleteAudience(Integer id, UserDetails userDetails);
    String updateRolesOfAudience(Integer id, Set<RoleName> roleNames, UserDetails userDetails);
    Audience findAudienceById(Integer id);
}
