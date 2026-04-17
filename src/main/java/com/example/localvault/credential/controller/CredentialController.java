package com.example.localvault.credential.controller;

import com.example.localvault.credential.domain.CredentialType;
import com.example.localvault.credential.dto.CredentialRequest;
import com.example.localvault.credential.dto.CredentialResponse;
import com.example.localvault.credential.service.CredentialService;
import com.example.localvault.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;


@Tag(name = "Credential", description = "비밀번호 관리 API")
@RestController
@RequestMapping("/api/v1/credentials")
@RequiredArgsConstructor
public class CredentialController {

    private final CredentialService credentialService;

    @Operation(summary = "비밀번호 등록")
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> create(
            @Valid @RequestBody CredentialRequest request,
            @AuthenticationPrincipal Long memberId) {
        Long id = credentialService.create(request, memberId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(id));
    }

    @Operation(summary = "비밀번호 목록 조회 (마스킹)")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<CredentialResponse>>> getList(
            @RequestParam(value = "type", required = false) CredentialType type,
            @RequestParam(value = "keyword", required = false) String keyword,
            @AuthenticationPrincipal Long memberId,
            Pageable pageable) {
        Page<CredentialResponse> list = credentialService.getList(type, keyword, memberId, pageable);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @Operation(summary = "비밀번호 상세 조회 (복호화)")
    @GetMapping("/{id}/reveal")
    public ResponseEntity<ApiResponse<CredentialResponse>> reveal(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal Long memberId,
            HttpServletRequest request) {
        CredentialResponse response = credentialService.reveal(id, memberId, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @Operation(summary = "비밀번호 수정")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> update(
            @PathVariable("id") Long id,
            @Valid @RequestBody CredentialRequest request) {
        credentialService.update(id, request);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @Operation(summary = "비밀번호 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable("id") Long id) {
        credentialService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}