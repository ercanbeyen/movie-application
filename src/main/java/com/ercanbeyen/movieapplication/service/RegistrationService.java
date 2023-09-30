package com.ercanbeyen.movieapplication.service;

import com.ercanbeyen.movieapplication.dto.request.auth.RegistrationRequest;

public interface RegistrationService {
    String registerAudience(RegistrationRequest request);
}
