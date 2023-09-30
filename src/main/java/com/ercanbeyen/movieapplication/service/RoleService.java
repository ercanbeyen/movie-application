package com.ercanbeyen.movieapplication.service;

import com.ercanbeyen.movieapplication.constant.enums.RoleName;
import com.ercanbeyen.movieapplication.entity.Role;

public interface RoleService {
    Role getRoleByRoleName(RoleName roleName);
}
