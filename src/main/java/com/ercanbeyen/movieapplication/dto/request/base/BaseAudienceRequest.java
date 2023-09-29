package com.ercanbeyen.movieapplication.dto.request.base;

import com.ercanbeyen.movieapplication.constant.message.ValidationMessages;
import com.ercanbeyen.movieapplication.constant.names.FieldNames;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@MappedSuperclass
public class BaseAudienceRequest {
    @NotBlank(message = FieldNames.USERNAME + ValidationMessages.SHOULD_NOT_BLANK)
    private String username;
    @NotBlank(message = FieldNames.PASSWORD + ValidationMessages.SHOULD_NOT_BLANK)
    private String password;
    @NotBlank(message = FieldNames.NAME + ValidationMessages.SHOULD_NOT_BLANK)
    private String name;
    @NotBlank(message = FieldNames.SURNAME + ValidationMessages.SHOULD_NOT_BLANK)
    private String surname;
    @NotBlank(message = FieldNames.NATIONALITY + ValidationMessages.SHOULD_NOT_BLANK)
    private String nationality;
    private LocalDate birthYear;
    private String biography;
}
