package com.ercanbeyen.movieapplication.service;

import com.ercanbeyen.movieapplication.dto.AudienceDto;
import com.ercanbeyen.movieapplication.dto.request.create.CreateAudienceRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateActorRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateAudienceRequest;

public interface AudienceService {
    AudienceDto createAudience(CreateAudienceRequest request);
    AudienceDto getAudience(Integer id);
    AudienceDto updateAudience(Integer id, UpdateAudienceRequest request);
    void deleteAudience(Integer id);
}
