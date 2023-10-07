package com.ercanbeyen.movieapplication.dto.request.auth;

import com.ercanbeyen.movieapplication.constant.message.ValidationMessages;
import com.ercanbeyen.movieapplication.constant.names.FieldNames;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record RegistrationRequest(
        @NotBlank(message = FieldNames.USERNAME + ValidationMessages.SHOULD_NOT_BLANK) String username,
        @NotBlank(message = FieldNames.PASSWORD + ValidationMessages.SHOULD_NOT_BLANK) String password,
        @NotBlank(message = FieldNames.NAME + ValidationMessages.SHOULD_NOT_BLANK) String name,
        @NotBlank(message = FieldNames.SURNAME + ValidationMessages.SHOULD_NOT_BLANK) String surname,
        @NotBlank(message = FieldNames.NATIONALITY + ValidationMessages.SHOULD_NOT_BLANK) String nationality,
        LocalDate birthDate, String biography) {

}
