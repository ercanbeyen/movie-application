package com.ercanbeyen.movieapplication.service;

import com.ercanbeyen.movieapplication.constant.enums.RoleName;
import com.ercanbeyen.movieapplication.dto.RoleDto;
import com.ercanbeyen.movieapplication.dto.request.create.CreateRoleRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateRoleRequest;
import com.ercanbeyen.movieapplication.entity.Role;

import java.util.List;

public interface RoleService {
    RoleDto createRole(CreateRoleRequest request);
    List<RoleDto> getRoles();
    RoleDto getRole(Integer id);
    RoleDto updateRole(Integer id, UpdateRoleRequest request);
    String deleteRole(Integer id);
    Role findRoleByRoleName(RoleName roleName);
}
