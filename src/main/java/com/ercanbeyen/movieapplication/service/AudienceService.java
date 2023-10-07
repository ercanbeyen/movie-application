package com.ercanbeyen.movieapplication.service;

import com.ercanbeyen.movieapplication.dto.AudienceDto;
import com.ercanbeyen.movieapplication.dto.request.auth.RegistrationRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateAudienceRequest;
import org.springframework.security.core.userdetails.UserDetails;

public interface AudienceService {
    void createAudience(RegistrationRequest request);
    AudienceDto getAudience(Integer id);
    AudienceDto updateAudience(Integer id, UpdateAudienceRequest request, UserDetails userDetails);
    void deleteAudience(Integer id, UserDetails userDetails);
}
