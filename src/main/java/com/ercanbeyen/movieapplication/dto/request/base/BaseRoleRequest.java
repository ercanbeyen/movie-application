package com.ercanbeyen.movieapplication.dto.request.base;

import com.ercanbeyen.movieapplication.constant.enums.RoleName;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public class BaseRoleRequest {
    private RoleName roleName;
}
