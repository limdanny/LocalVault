package com.example.localvault.auth.service;

import com.example.localvault.auth.dto.LoginRequest;
import com.example.localvault.auth.dto.SignupRequest;
import com.example.localvault.auth.dto.TokenResponse;
import com.example.localvault.auth.jwt.JwtTokenProvider;
import com.example.localvault.global.exception.BusinessException;
import com.example.localvault.global.exception.ErrorCode;
import com.example.localvault.member.domain.Member;
import com.example.localvault.member.domain.MemberRole;
import com.example.localvault.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    // 회원가입
    @Transactional
    public Long signup(SignupRequest request) {
        // 이메일 중복 확인
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.VALID_INVALID_EMAIL);
        }

        Member member = Member.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .role(MemberRole.USER)
                .build();

        Member saved = memberRepository.save(member);
        return saved.getId();
    }

    // 로그인
    public TokenResponse login(LoginRequest request) {
        // 사용자 조회
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_INVALID_CREDENTIALS));

        // 비밀번호 확인
        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new BusinessException(ErrorCode.AUTH_WRONG_PASSWORD);
        }

        // 계정 상태 확인
        if (member.getStatus() == com.example.localvault.member.domain.MemberStatus.INACTIVE) {
            throw new BusinessException(ErrorCode.AUTH_INACTIVE_ACCOUNT);
        }

        // 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(member.getId(), member.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getId());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessExpiration() / 1000)
                .member(TokenResponse.MemberInfo.builder()
                        .id(member.getId())
                        .email(member.getEmail())
                        .name(member.getName())
                        .role(member.getRole().name())
                        .build())
                .build();
    }
}