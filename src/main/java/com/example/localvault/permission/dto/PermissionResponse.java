package com.example.localvault.permission.dto;

import com.example.localvault.permission.domain.Permission;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PermissionResponse {

    private Long id;
    private Long memberId;
    private String memberName;
    private String memberEmail;
    private Long credentialId;
    private String serviceName;
    private LocalDateTime grantedAt;
    private String grantedByName;

    public static PermissionResponse of(Permission permission) {
        return PermissionResponse.builder()
                .id(permission.getId())
                .memberId(permission.getMember().getId())
                .memberName(permission.getMember().getName())
                .memberEmail(permission.getMember().getEmail())
                .credentialId(permission.getCredential().getId())
                .serviceName(permission.getCredential().getServiceName())
                .grantedAt(permission.getGrantedAt())
                .grantedByName(permission.getGrantedBy().getName())
                .build();
    }
}