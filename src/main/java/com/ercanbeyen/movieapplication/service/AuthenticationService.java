package com.ercanbeyen.movieapplication.service;

import com.ercanbeyen.movieapplication.constant.enums.RoleName;

import java.util.List;

public interface AuthenticationService {
    String getUsername();
    List<RoleName> getRoles();
}
