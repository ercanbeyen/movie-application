package com.ercanbeyen.movieapplication.service;

import com.ercanbeyen.movieapplication.dto.RoleDto;
import com.ercanbeyen.movieapplication.dto.request.create.CreateRoleRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateRoleRequest;
import com.ercanbeyen.movieapplication.entity.Role;

import java.util.List;

public interface RoleService {
    String createRole(CreateRoleRequest request);
    List<RoleDto> getRoles();
    RoleDto getRole(Integer id);
    String updateRole(Integer id, UpdateRoleRequest request);
    String deleteRole(Integer id);
    Role findRole(String roleName);
}
