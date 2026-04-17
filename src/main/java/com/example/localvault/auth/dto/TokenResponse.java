package com.example.localvault.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TokenResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;
    private MemberInfo member;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class MemberInfo {
        private Long id;
        private String email;
        private String name;
        private String role;
    }
}