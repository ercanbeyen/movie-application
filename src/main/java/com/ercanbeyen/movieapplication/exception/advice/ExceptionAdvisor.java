package com.ercanbeyen.movieapplication.exception.advice;

import com.ercanbeyen.movieapplication.exception.ResourceAlreadyExists;
import com.ercanbeyen.movieapplication.util.ResponseHandler;
import com.ercanbeyen.movieapplication.exception.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
public class ExceptionAdvisor extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getAllErrors()
                .forEach(
                        error -> {
                            String fieldName = ((FieldError) error).getField();
                            String errorMessage = error.getDefaultMessage();
                            errors.put(fieldName, errorMessage);
                        });

        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, null, errors);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(Exception exception) {
        return ResponseHandler.generateResponse(HttpStatus.NOT_FOUND, exception.getMessage(), null);
    }

    @ExceptionHandler(ResourceAlreadyExists.class)
    public ResponseEntity<?> handleResourceAlreadyExists(Exception exception) {
        return ResponseHandler.generateResponse(HttpStatus.CONFLICT, exception.getMessage(), null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneralException(Exception exception) {
        return ResponseHandler.generateResponse(HttpStatus.EXPECTATION_FAILED, exception.getMessage(), null);
    }
}
