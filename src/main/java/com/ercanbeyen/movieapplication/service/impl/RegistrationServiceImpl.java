package com.ercanbeyen.movieapplication.service.impl;

import com.ercanbeyen.movieapplication.constant.message.ActionMessages;
import com.ercanbeyen.movieapplication.constant.message.LogMessages;
import com.ercanbeyen.movieapplication.constant.message.ResponseMessages;
import com.ercanbeyen.movieapplication.constant.names.ResourceNames;
import com.ercanbeyen.movieapplication.dto.AudienceDto;
import com.ercanbeyen.movieapplication.dto.request.auth.RegistrationRequest;
import com.ercanbeyen.movieapplication.service.AudienceService;
import com.ercanbeyen.movieapplication.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationServiceImpl implements RegistrationService {
    private final AudienceService audienceService;

    @Override
    public String registerAudience(RegistrationRequest request) {
        log.info(LogMessages.STARTED, "registerAudience");
        AudienceDto audienceDto = audienceService.createAudience(request);
        return String.format(ResponseMessages.SUCCESS, ResourceNames.AUDIENCE, audienceDto.id(), ActionMessages.REGISTERED);
    }
}
