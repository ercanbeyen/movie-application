package com.ercanbeyen.movieapplication.dto.request.base;

import com.ercanbeyen.movieapplication.constant.enums.RoleName;
import com.ercanbeyen.movieapplication.constant.message.ValidationMessages;
import com.ercanbeyen.movieapplication.constant.names.ResourceNames;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public class BaseRoleRequest {
    @NotBlank(message = ResourceNames.ROLE + ValidationMessages.SHOULD_NOT_BLANK)
    private String role;
}
