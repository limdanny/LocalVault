package com.example.localvault.credential.repository;

import com.example.localvault.credential.domain.Credential;
import com.example.localvault.credential.domain.CredentialType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CredentialRepository extends JpaRepository<Credential, Long> {

    @Query("SELECT c FROM Credential c WHERE c.deleted = false " +
           "AND (:type IS NULL OR c.credentialType = :type) " +
           "AND (:keyword IS NULL OR c.serviceName LIKE %:keyword%)")
    Page<Credential> findAllByCondition(@Param("type") CredentialType type,
                                        @Param("keyword") String keyword,
                                        Pageable pageable);
}