package com.ercanbeyen.movieapplication.constant.annotation;

import com.ercanbeyen.movieapplication.validator.ImdbIdValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ImdbIdValidator.class)
public @interface ImdbIdRequest {
    String message() default "Invalid imdbId";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
