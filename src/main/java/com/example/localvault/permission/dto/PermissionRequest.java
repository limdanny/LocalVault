package com.example.localvault.permission.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class PermissionRequest {

    @NotNull(message = "사용자 ID는 필수입니다")
    private Long memberId;

    @NotNull(message = "비밀번호 ID 목록은 필수입니다")
    private List<Long> credentialIds;
}