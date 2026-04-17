package com.example.localvault.auth.jwt;

import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.localvault.member.domain.MemberRole;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long accessExpiration;
    private final long refreshExpiration;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-expiration}") long accessExpiration,
            @Value("${jwt.refresh-expiration}") long refreshExpiration) {

        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessExpiration = accessExpiration;
        this.refreshExpiration = refreshExpiration;
    }

    // Access Token 생성
    public String createAccessToken(Long memberId, MemberRole role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessExpiration);

        return Jwts.builder()
                .subject(memberId.toString())
                .claim("role", role.name())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    // Refresh Token 생성
    public String createRefreshToken(Long memberId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshExpiration);

        return Jwts.builder()
                .subject(memberId.toString())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // 토큰에서 memberId 추출
    public Long getMemberId(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return Long.parseLong(claims.getSubject());
    }

    // 토큰에서 role 추출
    public String getRole(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.get("role", String.class);
    }

    public long getAccessExpiration() {
        return accessExpiration;
    }
}