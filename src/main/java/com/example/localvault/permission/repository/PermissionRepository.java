package com.example.localvault.permission.repository;

import com.example.localvault.permission.domain.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

    // 사용자가 특정 비밀번호에 권한 있는지
    boolean existsByMemberIdAndCredentialId(Long memberId, Long credentialId);

    // 사용자의 모든 권한
    List<Permission> findByMemberId(Long memberId);

    // 특정 비밀번호에 대한 모든 권한
    List<Permission> findByCredentialId(Long credentialId);

    // 권한 삭제
    void deleteByMemberIdAndCredentialId(Long memberId, Long credentialId);

    // 사용자가 접근 가능한 비밀번호 ID 목록
    @Query("SELECT p.credential.id FROM Permission p WHERE p.member.id = :memberId")
    List<Long> findCredentialIdsByMemberId(@Param("memberId") Long memberId);
}