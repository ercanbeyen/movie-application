package com.ercanbeyen.movieapplication.service.impl;

import com.ercanbeyen.movieapplication.constant.enums.RoleName;
import com.ercanbeyen.movieapplication.constant.message.LogMessages;
import com.ercanbeyen.movieapplication.constant.message.ResponseMessages;
import com.ercanbeyen.movieapplication.constant.names.ResourceNames;
import com.ercanbeyen.movieapplication.dto.RoleDto;
import com.ercanbeyen.movieapplication.dto.converter.RoleDtoConverter;
import com.ercanbeyen.movieapplication.dto.request.create.CreateRoleRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateRoleRequest;
import com.ercanbeyen.movieapplication.entity.Role;
import com.ercanbeyen.movieapplication.exception.ResourceConflictException;
import com.ercanbeyen.movieapplication.exception.ResourceNotFoundException;
import com.ercanbeyen.movieapplication.repository.RoleRepository;
import com.ercanbeyen.movieapplication.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final RoleDtoConverter roleDtoConverter;

    @Override
    public RoleDto createRole(CreateRoleRequest request) {
        RoleName roleName = request.getRoleName();
        Optional<Role> roleInDb = roleRepository.findByRoleName(roleName);

        if (roleInDb.isPresent()) {
            throw new ResourceConflictException(String.format(ResponseMessages.ALREADY_EXISTS, ResourceNames.ROLE));
        }

        Role newRole = new Role();
        newRole.setRoleName(roleName);
        Role createdRole = roleRepository.save(newRole);

        return roleDtoConverter.convert(createdRole);
    }

    @Override
    public List<RoleDto> getRoles() {
        return roleRepository.findAll()
                .stream()
                .map(roleDtoConverter::convert)
                .toList();
    }

    @Override
    public RoleDto getRole(Integer id) {
        Role roleInDb = findRoleById(id);
        return roleDtoConverter.convert(roleInDb);
    }

    @Override
    public RoleDto updateRole(Integer id, UpdateRoleRequest request) {
        Role roleInDb = findRoleById(id);
        RoleName roleName = request.getRoleName();
        Role updatedRole;

        try {
            roleInDb.setRoleName(roleName);
            updatedRole = roleRepository.save(roleInDb);
            log.info(LogMessages.SAVED, ResourceNames.ROLE);
        } catch (Exception exception) {
            log.error("{} {} is not found", ResourceNames.ROLE, request.getRoleName());
            throw new ResourceConflictException(String.format(ResponseMessages.ALREADY_EXISTS, ResourceNames.ROLE));
        }

        return roleDtoConverter.convert(updatedRole);
    }

    @Override
    public String deleteRole(Integer id) {
        Role roleInDb = findRoleById(id);

        if (!roleInDb.getAudiences().isEmpty()) {
            log.error("{} {} includes {}s", ResourceNames.ROLE, id, ResourceNames.AUDIENCE);
            throw new ResourceConflictException(ResourceNames.ROLE + " cannot be deleted unless it does not include any " + ResourceNames.AUDIENCE);
        }

        roleRepository.deleteById(id);

        return ResponseMessages.SUCCESS;
    }

    @Override
    public Role findRoleByRoleName(RoleName roleName) {
        return roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, ResourceNames.ROLE)));
    }

    private Role findRoleById(Integer id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, ResourceNames.ROLE)));
    }
}
