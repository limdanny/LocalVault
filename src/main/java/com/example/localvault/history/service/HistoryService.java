package com.example.localvault.history.service;

import com.example.localvault.credential.domain.Credential;
import com.example.localvault.credential.repository.CredentialRepository;
import com.example.localvault.global.exception.BusinessException;
import com.example.localvault.global.exception.ErrorCode;
import com.example.localvault.history.domain.AccessHistory;
import com.example.localvault.history.domain.ActionType;
import com.example.localvault.history.dto.HistoryResponse;
import com.example.localvault.history.repository.AccessHistoryRepository;
import com.example.localvault.member.domain.Member;
import com.example.localvault.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HistoryService {

    private final AccessHistoryRepository historyRepository;
    private final MemberRepository memberRepository;
    private final CredentialRepository credentialRepository;

    // 이력 기록
    @Transactional
    public void record(Long memberId, Long credentialId, ActionType actionType,
                       String ipAddress, String userAgent) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PERM_MEMBER_NOT_FOUND));

        Credential credential = credentialRepository.findById(credentialId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PERM_CREDENTIAL_NOT_FOUND));

        AccessHistory history = AccessHistory.builder()
                .member(member)
                .credential(credential)
                .actionType(actionType)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();

        historyRepository.save(history);
    }

    // 이력 조회
    public Page<HistoryResponse> getList(Long memberId, Long credentialId,
                                         LocalDateTime startDate, LocalDateTime endDate,
                                         Pageable pageable) {
        return historyRepository.findByCondition(memberId, credentialId, startDate, endDate, pageable)
                .map(HistoryResponse::of);
    }
}