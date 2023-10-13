package com.ercanbeyen.movieapplication.controller;

import com.ercanbeyen.movieapplication.constant.annotation.LogExecutionTime;
import com.ercanbeyen.movieapplication.dto.RoleDto;
import com.ercanbeyen.movieapplication.dto.request.create.CreateRoleRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateRoleRequest;
import com.ercanbeyen.movieapplication.service.RoleService;
import com.ercanbeyen.movieapplication.util.ResponseHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @PostMapping
    public ResponseEntity<?> createRole(@RequestBody CreateRoleRequest request) {
        RoleDto roleDto = roleService.createRole(request);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, roleDto);
    }

    @LogExecutionTime
    @GetMapping
    public ResponseEntity<?> getRoles() {
        List<RoleDto> roleDtoList = roleService.getRoles();
        return ResponseHandler.generateResponse(HttpStatus.OK, null, roleDtoList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRole(@PathVariable Integer id) {
        RoleDto roleDto = roleService.getRole(id);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, roleDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRole(@PathVariable Integer id, @RequestBody UpdateRoleRequest request) {
        RoleDto roleDto = roleService.updateRole(id, request);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, roleDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable Integer id) {
        String message = roleService.deleteRole(id);
        return ResponseHandler.generateResponse(HttpStatus.OK, message, null);
    }
}
