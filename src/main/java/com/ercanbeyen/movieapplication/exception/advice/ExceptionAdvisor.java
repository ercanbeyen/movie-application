package com.ercanbeyen.movieapplication.exception.advice;

import com.ercanbeyen.movieapplication.exception.EntityNotFound;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ExceptionAdvisor extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EntityNotFound.class)
    public ResponseEntity<?> handleEntityNotFoundException(Exception exception) {
        /*Response response = Response.builder()
                .status(HttpStatus.NOT_FOUND)
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);*/
        return null;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleEntityGeneralException(Exception exception) {
        /*Response response = Response.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);*/
        return null;
    }
}
