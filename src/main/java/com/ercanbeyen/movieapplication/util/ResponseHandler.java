package com.ercanbeyen.movieapplication.util;

import com.ercanbeyen.movieapplication.constant.names.ParameterNames;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ResponseHandler {
    public static ResponseEntity<Object> generateResponse(HttpStatus httpStatus, String message, Object data) {
        Map<String, Object> response = new HashMap<>();

        try {
            response.put(ParameterNames.STATUS, httpStatus);
            response.put(ParameterNames.MESSAGE, message);
            response.put(ParameterNames.DATA, data);
            response.put(ParameterNames.TIMESTAMP, LocalDateTime.now());
        } catch (Exception exception) {
            response.clear();
            response.put(ParameterNames.STATUS, HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put(ParameterNames.MESSAGE, exception.getMessage());
            response.put(ParameterNames.DATA, null);
            response.put(ParameterNames.TIMESTAMP, LocalDateTime.now());
        }

        return new ResponseEntity<>(response, httpStatus);
    }
}
