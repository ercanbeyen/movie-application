package com.ercanbeyen.movieapplication.validator;

import com.ercanbeyen.movieapplication.constant.annotation.ImdbIdRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImdbIdValidator implements ConstraintValidator<ImdbIdRequest, String> {
    @Override
    public void initialize(ImdbIdRequest constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String imdbId, ConstraintValidatorContext constraintValidatorContext) {
        String regex = "^ev\\d{7}\\d{4}(-\\d)?$|^(ch|co|ev|nm|tt)\\d{7}$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(imdbId);

        return matcher.find();
    }
}
