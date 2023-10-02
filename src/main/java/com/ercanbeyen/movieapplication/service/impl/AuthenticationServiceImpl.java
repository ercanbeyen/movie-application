package com.ercanbeyen.movieapplication.service.impl;

import com.ercanbeyen.movieapplication.constant.enums.RoleName;
import com.ercanbeyen.movieapplication.service.AuthenticationService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    @Override
    public String getUsername() {
        return getUser().getUsername();
    }

    @Override
    public List<RoleName> getRoles() {
        Collection<GrantedAuthority> grantedAuthorities = getUser().getAuthorities();
        List<RoleName> roleNameList = new ArrayList<>();

        for (GrantedAuthority grantedAuthority : grantedAuthorities) {
            String roleName = grantedAuthority.getAuthority();
            roleNameList.add(RoleName.valueOf(roleName));
        }

        return roleNameList;
    }

    private User getUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
