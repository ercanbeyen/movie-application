package com.ercanbeyen.movieapplication.dto.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ResponseHandler {
    public static ResponseEntity<Object> generateResponse(HttpStatus httpStatus, String message, Object data) {
        Map<String, Object> response = new HashMap<>();

        try {
            response.put("status", httpStatus);
            response.put("message", message);
            response.put("data", data);
            response.put("timestamp", LocalDateTime.now());
        } catch (Exception exception) {
            response.clear();
            response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("message", exception.getMessage());
            response.put("data", null);
            response.put("timestamp", LocalDateTime.now());
        }

        return new ResponseEntity<>(response, httpStatus);
    }
}
