package com.example.localvault.history.repository;

import com.example.localvault.history.domain.AccessHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface AccessHistoryRepository extends JpaRepository<AccessHistory, Long> {

    @Query("SELECT h FROM AccessHistory h " +
           "WHERE (:memberId IS NULL OR h.member.id = :memberId) " +
           "AND (:credentialId IS NULL OR h.credential.id = :credentialId) " +
           "AND (:startDate IS NULL OR h.accessedAt >= :startDate) " +
           "AND (:endDate IS NULL OR h.accessedAt <= :endDate) " +
           "ORDER BY h.accessedAt DESC")
    Page<AccessHistory> findByCondition(
            @Param("memberId") Long memberId,
            @Param("credentialId") Long credentialId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);
}