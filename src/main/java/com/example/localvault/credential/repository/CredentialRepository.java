package com.example.localvault.credential.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.localvault.credential.domain.Credential;
import com.example.localvault.credential.domain.CredentialType;

public interface CredentialRepository extends JpaRepository<Credential, Long> {

    // 관리자용 - 전체 조회
    @Query("SELECT c FROM Credential c WHERE c.deleted = false " +
           "AND (:type IS NULL OR c.credentialType = :type) " +
           "AND (:keyword IS NULL OR c.serviceName LIKE %:keyword%)")
    Page<Credential> findAllByCondition(@Param("type") CredentialType type,
                                        @Param("keyword") String keyword,
                                        Pageable pageable);

    // 일반 사용자용 - 권한 있는 것만
    @Query("SELECT c FROM Credential c JOIN Permission p ON c.id = p.credential.id " +
           "WHERE c.deleted = false AND p.member.id = :memberId " +
           "AND (:type IS NULL OR c.credentialType = :type) " +
           "AND (:keyword IS NULL OR c.serviceName LIKE %:keyword%)")
    Page<Credential> findAllByConditionAndMemberId(@Param("type") CredentialType type,
                                                    @Param("keyword") String keyword,
                                                    @Param("memberId") Long memberId,
                                                    Pageable pageable);
}