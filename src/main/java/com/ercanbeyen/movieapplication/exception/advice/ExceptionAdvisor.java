package com.ercanbeyen.movieapplication.exception.advice;

import com.ercanbeyen.movieapplication.dto.response.ResponseHandler;
import com.ercanbeyen.movieapplication.exception.EntityNotFound;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@RestControllerAdvice
public class ExceptionAdvisor extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EntityNotFound.class)
    public ResponseEntity<?> handleEntityNotFoundException(Exception exception) {
        return ResponseHandler.generateResponse(HttpStatus.NOT_FOUND, exception.getMessage(), null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleEntityGeneralException(Exception exception) {
        return ResponseHandler.generateResponse(HttpStatus.EXPECTATION_FAILED, exception.getMessage(), null);
    }
}
