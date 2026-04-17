package com.example.localvault.history.dto;

import com.example.localvault.history.domain.AccessHistory;
import com.example.localvault.history.domain.ActionType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class HistoryResponse {

    private Long id;
    private Long memberId;
    private String memberName;
    private String memberEmail;
    private Long credentialId;
    private String serviceName;
    private ActionType actionType;
    private String ipAddress;
    private LocalDateTime accessedAt;

    public static HistoryResponse of(AccessHistory history) {
        return HistoryResponse.builder()
                .id(history.getId())
                .memberId(history.getMember().getId())
                .memberName(history.getMember().getName())
                .memberEmail(history.getMember().getEmail())
                .credentialId(history.getCredential().getId())
                .serviceName(history.getCredential().getServiceName())
                .actionType(history.getActionType())
                .ipAddress(history.getIpAddress())
                .accessedAt(history.getAccessedAt())
                .build();
    }
}