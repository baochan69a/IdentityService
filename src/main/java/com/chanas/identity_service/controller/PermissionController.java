package com.chanas.identity_service.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.chanas.identity_service.dto.request.ApiResponse;
import com.chanas.identity_service.dto.request.PermissionRequest;
import com.chanas.identity_service.dto.response.PermissionResponse;
import com.chanas.identity_service.service.PermissionService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionController {
    PermissionService permissionService;

    @PostMapping
    ApiResponse<PermissionResponse> create(@RequestBody PermissionRequest request) {
        return ApiResponse.<PermissionResponse>builder()
                .result(permissionService.create(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<PermissionResponse>> getAll() {
        return ApiResponse.<List<PermissionResponse>>builder()
                .result(permissionService.getAll())
                .build();
    }

    @DeleteMapping("/{perrmission}")
    ApiResponse<Void> delete(@PathVariable String perrmission) {
        permissionService.delete(perrmission);
        return ApiResponse.<Void>builder().build();
    }
}
