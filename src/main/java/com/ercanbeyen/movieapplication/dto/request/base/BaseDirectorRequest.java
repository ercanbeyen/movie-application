package com.ercanbeyen.movieapplication.dto.request.base;

import com.ercanbeyen.movieapplication.constant.message.ResponseMessages;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseDirectorRequest {
    @NotBlank(message = "Name" + ResponseMessages.SHOULD_NOT_BLANK)
    private String name;
    @NotBlank(message = "Surname" + ResponseMessages.SHOULD_NOT_BLANK)
    private String surname;
    @NotBlank(message = "Nationality" + ResponseMessages.SHOULD_NOT_BLANK)
    private String nationality;
    private LocalDate birthYear;
    private String biography;
}
