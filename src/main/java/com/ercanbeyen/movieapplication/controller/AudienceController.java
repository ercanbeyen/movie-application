package com.ercanbeyen.movieapplication.controller;

import com.ercanbeyen.movieapplication.annotation.SelfAuthentication;
import com.ercanbeyen.movieapplication.dto.AudienceDto;
import com.ercanbeyen.movieapplication.dto.PageDto;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateAudienceRequest;
import com.ercanbeyen.movieapplication.entity.Audience;
import com.ercanbeyen.movieapplication.service.AudienceService;
import com.ercanbeyen.movieapplication.util.ResponseHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/audiences")
@RequiredArgsConstructor
public class AudienceController {
    private final AudienceService audienceService;

    @GetMapping
    public ResponseEntity<?> getAudiences(Pageable pageable) {
        PageDto<Audience, AudienceDto> audienceDtoPage = audienceService.getAudiences(pageable);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, audienceDtoPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAudience(@PathVariable Integer id) {
        AudienceDto audienceDto = audienceService.getAudience(id);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, audienceDto);
    }

    @GetMapping("/find")
    public ResponseEntity<?> getAudience(@RequestParam(name = "user") String username) {
        AudienceDto audienceDto = audienceService.getAudience(username);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, audienceDto);
    }

    @SelfAuthentication
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAudience(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Integer id, @RequestBody @Valid UpdateAudienceRequest request) {
        AudienceDto audienceDto = audienceService.updateAudience(id, request, userDetails);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, audienceDto);
    }

    @SelfAuthentication
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAudience(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Integer id) {
        audienceService.deleteAudience(id, userDetails);
        return ResponseHandler.generateResponse(HttpStatus.NO_CONTENT, null, null);
    }

    @PatchMapping("/{id}/roles")
    public ResponseEntity<?> updateRolesOfAudience(@PathVariable Integer id, @RequestBody Set<String> roleNames, @AuthenticationPrincipal UserDetails userDetails) {
        String message = audienceService.updateRolesOfAudience(id, roleNames, userDetails);
        return ResponseHandler.generateResponse(HttpStatus.OK, message, null);
    }
}
