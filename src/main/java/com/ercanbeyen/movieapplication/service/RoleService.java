package com.ercanbeyen.movieapplication.service;

import com.ercanbeyen.movieapplication.constant.enums.RoleName;
import com.ercanbeyen.movieapplication.dto.RoleDto;
import com.ercanbeyen.movieapplication.dto.request.create.CreateRoleRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateRoleRequest;
import com.ercanbeyen.movieapplication.entity.Role;

public interface RoleService {
    RoleDto createRole(CreateRoleRequest request);
    RoleDto updateRole(Integer id, UpdateRoleRequest request);
    String deleteRole(Integer id);
    Role findRoleByRoleName(RoleName roleName);
}
