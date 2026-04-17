package com.example.localvault.credential.domain;

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
public class Credential {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String serviceName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CredentialType credentialType;

    @Column(length = 500)
    private String url;

    @Column(nullable = false, length = 100)
    private String username;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String encryptedPassword;

    @Column(nullable = false, length = 50)
    private String iv;

    private Integer port;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private boolean deleted = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private Member createdBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Builder
    public Credential(String serviceName, CredentialType credentialType, String url,
                      String username, String encryptedPassword, String iv,
                      Integer port, String description, Member createdBy) {
        this.serviceName = serviceName;
        this.credentialType = credentialType;
        this.url = url;
        this.username = username;
        this.encryptedPassword = encryptedPassword;
        this.iv = iv;
        this.port = port;
        this.description = description;
        this.createdBy = createdBy;
    }

    // 수정 메서드
    public void update(String serviceName, CredentialType credentialType, String url,
                       String username, String encryptedPassword, String iv,
                       Integer port, String description) {
        this.serviceName = serviceName;
        this.credentialType = credentialType;
        this.url = url;
        this.username = username;
        this.encryptedPassword = encryptedPassword;
        this.iv = iv;
        this.port = port;
        this.description = description;
    }

    // Soft Delete
    public void delete() {
        this.deleted = true;
    }
}