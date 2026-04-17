package com.example.localvault.permission.controller;

import com.example.localvault.global.response.ApiResponse;
import com.example.localvault.permission.dto.PermissionRequest;
import com.example.localvault.permission.dto.PermissionResponse;
import com.example.localvault.permission.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Permission", description = "권한 관리 API")
@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @Operation(summary = "권한 부여")
    @PostMapping
    public ResponseEntity<ApiResponse<Integer>> grant(
            @Valid @RequestBody PermissionRequest request,
            @AuthenticationPrincipal Long memberId) {
        int count = permissionService.grant(request, memberId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(count));
    }

    @Operation(summary = "권한 회수")
    @DeleteMapping
    public ResponseEntity<ApiResponse<Integer>> revoke(
            @Valid @RequestBody PermissionRequest request) {
        int count = permissionService.revoke(request);
        return ResponseEntity.ok(ApiResponse.ok(count));
    }

    @Operation(summary = "사용자별 권한 조회")
    @GetMapping("/members/{memberId}")
    public ResponseEntity<ApiResponse<List<PermissionResponse>>> getByMember(
    		@PathVariable("memberId") Long memberId) {
        List<PermissionResponse> list = permissionService.getByMember(memberId);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @Operation(summary = "비밀번호별 권한 조회")
    @GetMapping("/credentials/{credentialId}")
    public ResponseEntity<ApiResponse<List<PermissionResponse>>> getByCredential(
    		@PathVariable("credentialId") Long credentialId) {
        List<PermissionResponse> list = permissionService.getByCredential(credentialId);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }
}