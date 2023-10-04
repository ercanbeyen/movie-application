package com.ercanbeyen.movieapplication.controller;

import com.ercanbeyen.movieapplication.dto.AudienceDto;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateAudienceRequest;
import com.ercanbeyen.movieapplication.service.AudienceService;
import com.ercanbeyen.movieapplication.util.ResponseHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/audiences")
@RequiredArgsConstructor
public class AudienceController {
    private final AudienceService audienceService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getAudience(@PathVariable Integer id) {
        AudienceDto audienceDto = audienceService.getAudience(id);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, audienceDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAudience(@PathVariable Integer id, @RequestBody @Valid UpdateAudienceRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        AudienceDto audienceDto = audienceService.updateAudience(id, request, userDetails);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, audienceDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAudience(@PathVariable Integer id, @AuthenticationPrincipal UserDetails userDetails) {
        audienceService.deleteAudience(id, userDetails);
        return ResponseHandler.generateResponse(HttpStatus.NO_CONTENT, null, null);
    }
}
