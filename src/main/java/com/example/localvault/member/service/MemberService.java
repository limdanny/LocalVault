package com.example.localvault.member.service;

import com.example.localvault.global.exception.BusinessException;
import com.example.localvault.global.exception.ErrorCode;
import com.example.localvault.member.domain.Member;
import com.example.localvault.member.dto.MemberResponse;
import com.example.localvault.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    public List<MemberResponse> getAll() {
        return memberRepository.findAll().stream()
                .map(MemberResponse::of)
                .toList();
    }

    @Transactional
    public void activate(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.PERM_MEMBER_NOT_FOUND));
        member.activate();
    }

    @Transactional
    public void deactivate(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.PERM_MEMBER_NOT_FOUND));
        member.deactivate();
    }
}