package com.ercanbeyen.movieapplication.controller;

import com.ercanbeyen.movieapplication.dto.RoleDto;
import com.ercanbeyen.movieapplication.dto.request.create.CreateRoleRequest;
import com.ercanbeyen.movieapplication.dto.request.update.UpdateRoleRequest;
import com.ercanbeyen.movieapplication.service.RoleService;
import com.ercanbeyen.movieapplication.util.ResponseHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @PostMapping
    public ResponseEntity<?> createRole(@RequestBody @Valid CreateRoleRequest request) {
        RoleDto roleDto = roleService.createRole(request);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, roleDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRole(@PathVariable Integer id, @RequestBody @Valid UpdateRoleRequest request) {
        RoleDto roleDto = roleService.updateRole(id, request);
        return ResponseHandler.generateResponse(HttpStatus.OK, null, roleDto);
    }
}
