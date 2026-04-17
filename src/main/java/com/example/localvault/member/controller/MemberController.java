package com.example.localvault.member.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.localvault.global.response.ApiResponse;
import com.example.localvault.member.dto.MemberResponse;
import com.example.localvault.member.service.MemberService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Member", description = "사용자 관리 API")
@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "전체 사용자 목록")
    @GetMapping
    public ResponseEntity<ApiResponse<List<MemberResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(memberService.getAll()));
    }

    @Operation(summary = "사용자 승인")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<Void>> activate(@PathVariable("id") Long id) {
        memberService.activate(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @Operation(summary = "사용자 비활성화")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable("id") Long id) {
        memberService.deactivate(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}