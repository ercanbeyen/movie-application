package com.ercanbeyen.movieapplication.advice;

import com.ercanbeyen.movieapplication.exception.ResourceConflictException;
import com.ercanbeyen.movieapplication.exception.ResourceForbiddenException;
import com.ercanbeyen.movieapplication.util.ResponseHandler;
import com.ercanbeyen.movieapplication.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();

        exception.getBindingResult()
                .getAllErrors()
                .forEach(error -> {
                    String fieldName = ((FieldError) error).getField();
                    String errorMessage = error.getDefaultMessage();
                    errors.put(fieldName, errorMessage);
                });

        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, null, errors);
    }

    @ExceptionHandler(ResourceForbiddenException.class)
    public ResponseEntity<?> handleResourceForbiddenException(Exception exception) {
        return ResponseHandler.generateResponse(HttpStatus.FORBIDDEN, exception.getMessage(), null);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(Exception exception) {
        return ResponseHandler.generateResponse(HttpStatus.NOT_FOUND, exception.getMessage(), null);
    }

    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<?> handleResourceConflictException(Exception exception) {
        return ResponseHandler.generateResponse(HttpStatus.CONFLICT, exception.getMessage(), null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneralException(Exception exception) {
        return ResponseHandler.generateResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage(), null);
    }
}
