package com.example.localvault.permission.domain;

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
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"member_id", "credential_id"})
})
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credential_id", nullable = false)
    private Credential credential;

    @Column(nullable = false)
    private LocalDateTime grantedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "granted_by", nullable = false)
    private Member grantedBy;

    @PrePersist
    protected void onCreate() {
        grantedAt = LocalDateTime.now();
    }

    @Builder
    public Permission(Member member, Credential credential, Member grantedBy) {
        this.member = member;
        this.credential = credential;
        this.grantedBy = grantedBy;
    }
}