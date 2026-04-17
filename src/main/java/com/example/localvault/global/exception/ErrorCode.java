package com.example.localvault.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 인증
    AUTH_INVALID_CREDENTIALS("AUTH_001", "존재하지 않는 계정입니다"),
    AUTH_WRONG_PASSWORD("AUTH_002", "비밀번호가 일치하지 않습니다"),
    AUTH_INACTIVE_ACCOUNT("AUTH_003", "비활성화된 계정입니다"),
    AUTH_TOKEN_EXPIRED("AUTH_004", "만료된 토큰입니다"),
    AUTH_TOKEN_INVALID("AUTH_005", "유효하지 않은 토큰입니다"),
    AUTH_PENDING_ACCOUNT("AUTH_006", "승인 대기 중인 계정입니다"),

    // 권한
    PERM_ACCESS_DENIED("PERM_001", "권한이 없습니다"),
    PERM_MEMBER_NOT_FOUND("PERM_002", "존재하지 않는 사용자입니다"),
    PERM_CREDENTIAL_NOT_FOUND("PERM_003", "존재하지 않는 비밀번호입니다"),

    // 유효성
    VALID_MISSING_REQUIRED("VALID_001", "필수 항목이 누락되었습니다"),
    VALID_INVALID_EMAIL("VALID_002", "이메일 형식이 올바르지 않습니다"),

    // 암호화
    CRYPT_ERROR("CRYPT_001", "암호화 처리 중 오류가 발생했습니다");

    private final String code;
    private final String message;
}