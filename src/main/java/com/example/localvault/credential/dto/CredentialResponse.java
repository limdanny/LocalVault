package com.example.localvault.credential.dto;

import com.example.localvault.credential.domain.Credential;
import com.example.localvault.credential.domain.CredentialType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CredentialResponse {

    private Long id;
    private String serviceName;
    private CredentialType credentialType;
    private String url;
    private String username;
    private String password;  // 마스킹 또는 실제 값
    private Integer port;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 목록 조회용 (마스킹)
    public static CredentialResponse ofMasked(Credential credential) {
        return CredentialResponse.builder()
                .id(credential.getId())
                .serviceName(credential.getServiceName())
                .credentialType(credential.getCredentialType())
                .url(credential.getUrl())
                .username(credential.getUsername())
                .password("********")
                .port(credential.getPort())
                .description(credential.getDescription())
                .createdAt(credential.getCreatedAt())
                .updatedAt(credential.getUpdatedAt())
                .build();
    }

    // 상세 조회용 (복호화된 비밀번호)
    public static CredentialResponse ofRevealed(Credential credential, String decryptedPassword) {
        return CredentialResponse.builder()
                .id(credential.getId())
                .serviceName(credential.getServiceName())
                .credentialType(credential.getCredentialType())
                .url(credential.getUrl())
                .username(credential.getUsername())
                .password(decryptedPassword)
                .port(credential.getPort())
                .description(credential.getDescription())
                .createdAt(credential.getCreatedAt())
                .updatedAt(credential.getUpdatedAt())
                .build();
    }
}