package com.ercanbeyen.movieapplication.dto.request.auth;

import com.ercanbeyen.movieapplication.constant.message.ValidationMessages;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record RegistrationRequest(
        @NotBlank(message = "Username" + ValidationMessages.SHOULD_NOT_BLANK) String username,
        @NotBlank(message = "Password" + ValidationMessages.SHOULD_NOT_BLANK) String password,
        @NotBlank(message = "Name" + ValidationMessages.SHOULD_NOT_BLANK) String name,
        @NotBlank(message = "Surname" + ValidationMessages.SHOULD_NOT_BLANK) String surname,
        @NotBlank(message = "Nationality" + ValidationMessages.SHOULD_NOT_BLANK) String nationality,
        LocalDate birthYear, String biography) {

}
