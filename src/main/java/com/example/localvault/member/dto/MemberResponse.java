package com.example.localvault.member.dto;

import com.example.localvault.member.domain.Member;
import com.example.localvault.member.domain.MemberRole;
import com.example.localvault.member.domain.MemberStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MemberResponse {

    private Long id;
    private String email;
    private String name;
    private MemberRole role;
    private MemberStatus status;
    private LocalDateTime createdAt;

    public static MemberResponse of(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .role(member.getRole())
                .status(member.getStatus())
                .createdAt(member.getCreatedAt())
                .build();
    }
}