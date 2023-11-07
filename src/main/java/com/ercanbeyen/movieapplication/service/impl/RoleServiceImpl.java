package com.ercanbeyen.movieapplication.service.impl;

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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final RoleDtoConverter roleDtoConverter;

    @Override
    public String createRole(CreateRoleRequest request) {
        String roleName = request.getRoleName();

        roleRepository.findRole(roleName)
                .ifPresentOrElse(role -> {
                    throw new ResourceConflictException(String.format(ResponseMessages.ALREADY_EXISTS, ResourceNames.ROLE));
                    }, () -> {
                    Role newRole = new Role();
                    newRole.setRoleName(roleName);
                    roleRepository.save(newRole);
                });

        return ResponseMessages.SUCCESS;
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
        Role roleInDb =  roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, ResourceNames.ROLE)));
        return roleDtoConverter.convert(roleInDb);
    }

    @Transactional
    @Override
    public String updateRole(Integer id, UpdateRoleRequest request) {
        String roleName = request.getRoleName();

        roleRepository.updateRole(id, roleName);
        log.info(LogMessages.SAVED, ResourceNames.ROLE);

        return ResponseMessages.SUCCESS;
    }

    @Override
    public String deleteRole(Integer id) {
        roleRepository.findById(id)
                .ifPresentOrElse(role -> {
                    if (!role.getAudiences().isEmpty()) {
                        log.error("{} {} includes {}s", ResourceNames.ROLE, id, ResourceNames.AUDIENCE);
                        throw new ResourceConflictException(ResourceNames.ROLE + " cannot be deleted unless it does not include any " + ResourceNames.AUDIENCE);
                    }
                    roleRepository.delete(role);
                    }, () ->  {
                    throw new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, ResourceNames.ROLE));
                });

        return ResponseMessages.SUCCESS;
    }

    @Override
    public Role findRole(String roleName) {
        return roleRepository.findRole(roleName)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ResponseMessages.NOT_FOUND, ResourceNames.ROLE)));
    }

    @Async
    @Override
    public CompletableFuture<Role> findRoleAsync(String roleName) {
        return roleRepository.findByRoleName(roleName)
                .handle((result, exception) -> {
                    if (exception != null) {
                        throw new ResourceNotFoundException(String.format(String.format(ResponseMessages.NOT_FOUND, ResourceNames.ROLE)));
                    }

                    return result;
                });
    }
}
