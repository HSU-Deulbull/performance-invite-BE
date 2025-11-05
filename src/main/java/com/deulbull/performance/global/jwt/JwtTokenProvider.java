package com.deulbull.performance.global.jwt;

import com.deulbull.performance.domain.admin.entity.Admin;
import com.deulbull.performance.domain.admin.repository.AdminRepository;
import com.deulbull.performance.global.security.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final AdminRepository adminRepository;

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expiration; // 밀리초 단위

    private Key key;

    // @PostConstruct: @Value 주입이 끝난 후 실행됨
    // JWT 라이브러리(jjwt)는 단순 문자열이 아닌 서명 가능한 Key 객체를 요구함
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // 토큰 생성
    public String createToken(Admin admin) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expiration);

        return Jwts.builder()
                // subject: JWT에 담긴 정보의 주체. 일반적으로 사용자의 고유 식별자나 아이디 사용
                .subject(String.valueOf(admin.getId()))
                // claim: JWT 안에 넣고 싶은 추가 정보들
                .claim("performance", admin.getPerformance())
                .claim("role", admin.getRole())
                // issuedAt: 토큰이 발급된 시간 -> 보안상 이유로 보통 넣음
                .issuedAt(now)
                // expiration: 만료 시간
                .expiration(expiry)
                // signWith: 서명(Signature)을 위한 키 설정 (자동으로 알고리즘 선택됨 (현재: HS256))
                .signWith(key)
                // compact: 설정한 모든 정보들을 하나의 문자열(String) 토큰으로 압축해서 반환
                .compact();
    }

    // 토큰 유효성 검사 + 만료 검사
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // Claim 파싱
    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key)
                .build() // parser 생성
                .parseSignedClaims(token) // 서명 검증 + 만료 검증 + 파싱!!
                .getPayload(); // payload 부분(Claims)만 반환
    }

    // SecurityContext에 들어갈 인증 객체를 만드는 핵심 메서드
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        String adminId = claims.getSubject();

        // 1. DB에서 유저 조회
        Admin admin = adminRepository.findById(Long.valueOf(adminId))
                .orElseThrow(()-> new RuntimeException("해당 회원 정보 없음. adminId: " + adminId));

        // 2. CustomUserDetails 생성
        CustomUserDetails userDetails = new CustomUserDetails(admin);

        // 3. 인증 객체 생성 후 반환
        // principal: 사용자 정보 객체, credentials: 인증 수단, authorities: 권한 목록
        // UsernamePasswordAuthenticationToken: Spring Security에서 인증이 완료된 사용자를 SecurityContext에 저장하기 위해 사용하는 객체
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }
}