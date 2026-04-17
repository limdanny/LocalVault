package com.example.localvault.history.domain;

import com.example.localvault.credential.domain.Credential;
import com.example.localvault.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccessHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credential_id", nullable = false)
    private Credential credential;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActionType actionType;

    @Column(nullable = false, length = 50)
    private String ipAddress;

    @Column(length = 500)
    private String userAgent;

    @Column(nullable = false)
    private LocalDateTime accessedAt;

    @PrePersist
    protected void onCreate() {
        accessedAt = LocalDateTime.now();
    }

    @Builder
    public AccessHistory(Member member, Credential credential, ActionType actionType,
                         String ipAddress, String userAgent) {
        this.member = member;
        this.credential = credential;
        this.actionType = actionType;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
    }
}