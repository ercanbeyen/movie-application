package com.ercanbeyen.movieapplication.dto.request.base;

import com.ercanbeyen.movieapplication.constant.message.ValidationMessages;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@MappedSuperclass
public class BaseAudienceRequest {
    @NotBlank(message = "Username" + ValidationMessages.SHOULD_NOT_BLANK)
    private String username;
    @NotBlank(message = "Password" + ValidationMessages.SHOULD_NOT_BLANK)
    private String password;
    @NotBlank(message = "Name" + ValidationMessages.SHOULD_NOT_BLANK)
    private String name;
    @NotBlank(message = "Surname" + ValidationMessages.SHOULD_NOT_BLANK)
    private String surname;
    @NotBlank(message = "Nationality" + ValidationMessages.SHOULD_NOT_BLANK)
    private String nationality;
    private LocalDate birthYear;
    private String biography;
}
