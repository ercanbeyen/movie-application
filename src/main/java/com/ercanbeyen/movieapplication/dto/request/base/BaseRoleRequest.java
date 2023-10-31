package com.ercanbeyen.movieapplication.dto.request.base;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public class BaseRoleRequest {
    private String roleName;
}
