package com.example.localvault.permission.service;

import com.example.localvault.credential.domain.Credential;
import com.example.localvault.credential.repository.CredentialRepository;
import com.example.localvault.global.exception.BusinessException;
import com.example.localvault.global.exception.ErrorCode;
import com.example.localvault.member.domain.Member;
import com.example.localvault.member.repository.MemberRepository;
import com.example.localvault.permission.domain.Permission;
import com.example.localvault.permission.dto.PermissionRequest;
import com.example.localvault.permission.dto.PermissionResponse;
import com.example.localvault.permission.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final MemberRepository memberRepository;
    private final CredentialRepository credentialRepository;

    // 권한 부여
    @Transactional
    public int grant(PermissionRequest request, Long grantedById) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PERM_MEMBER_NOT_FOUND));

        Member grantedBy = memberRepository.findById(grantedById)
                .orElseThrow(() -> new BusinessException(ErrorCode.PERM_MEMBER_NOT_FOUND));

        int count = 0;
        for (Long credentialId : request.getCredentialIds()) {
            // 이미 권한 있으면 스킵
            if (permissionRepository.existsByMemberIdAndCredentialId(member.getId(), credentialId)) {
                continue;
            }

            Credential credential = credentialRepository.findById(credentialId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.PERM_CREDENTIAL_NOT_FOUND));

            Permission permission = Permission.builder()
                    .member(member)
                    .credential(credential)
                    .grantedBy(grantedBy)
                    .build();

            permissionRepository.save(permission);
            count++;
        }

        return count;
    }

    // 권한 회수
    @Transactional
    public int revoke(PermissionRequest request) {
        int count = 0;
        for (Long credentialId : request.getCredentialIds()) {
            if (permissionRepository.existsByMemberIdAndCredentialId(request.getMemberId(), credentialId)) {
                permissionRepository.deleteByMemberIdAndCredentialId(request.getMemberId(), credentialId);
                count++;
            }
        }
        return count;
    }

    // 사용자별 권한 조회
    public List<PermissionResponse> getByMember(Long memberId) {
        return permissionRepository.findByMemberId(memberId).stream()
                .map(PermissionResponse::of)
                .toList();
    }

    // 비밀번호별 권한 조회
    public List<PermissionResponse> getByCredential(Long credentialId) {
        return permissionRepository.findByCredentialId(credentialId).stream()
                .map(PermissionResponse::of)
                .toList();
    }

    // 권한 체크
    public boolean hasPermission(Long memberId, Long credentialId) {
        return permissionRepository.existsByMemberIdAndCredentialId(memberId, credentialId);
    }
}