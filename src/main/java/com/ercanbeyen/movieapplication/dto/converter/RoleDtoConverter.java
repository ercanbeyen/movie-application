package com.ercanbeyen.movieapplication.dto.converter;

import com.ercanbeyen.movieapplication.dto.RoleDto;
import com.ercanbeyen.movieapplication.entity.Role;
import org.springframework.stereotype.Component;

@Component
public class RoleDtoConverter {
    public RoleDto convert(Role role) {
        return new RoleDto(role.getId(), role.getRoleName());
    }
}
