package com.ercanbeyen.movieapplication.controller;

import com.ercanbeyen.movieapplication.dto.request.auth.RegistrationRequest;
import com.ercanbeyen.movieapplication.service.RegistrationService;
import com.ercanbeyen.movieapplication.util.ResponseHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/registration")
@RequiredArgsConstructor
public class RegistrationController {
    private final RegistrationService registrationService;
    @PostMapping
    public ResponseEntity<?> registerAudience(@RequestBody @Valid RegistrationRequest request) {
        String registrationResponse = registrationService.registerAudience(request);
        return ResponseHandler.generateResponse(HttpStatus.CREATED, registrationResponse, null);
    }
}
