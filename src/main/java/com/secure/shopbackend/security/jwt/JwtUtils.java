package com.secure.shopbackend.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    // jwt 비밀키(설정에서 가져옴)
    @Value("${spring.app.jwtSecret}")
    private String jwtSecret;
    // jwt 유효기간
    @Value("${spring.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    // HTTP 요청 헤더에서 JWT 토큰 추출
    public String getJwtFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        logger.debug("Authorization Header: {}", bearerToken);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 접두사 제거
        }
        return null;
    }

    // JWT 토큰 생성
    public String generateTokenFromUsername(UserDetails userDetails) {
        String username = userDetails.getUsername();
        // 사용자의 권한들을 콤마(,)로 연결하여 문자열로 생성
        String roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        return Jwts.builder()
                .setSubject(username)
                .claim("role", roles)       // role 클레임 추가했습니다.
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key())
                .compact();
    }

    // 토큰에서 유저네임 가져옴
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build()
                .parseSignedClaims(token)
                .getPayload().getSubject();
    }

    // JWT 토큰에서 "role" 클레임을 추출하는 메서드
    public String getRoleFromJwtToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith((SecretKey) key())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("role", String.class);
        } catch (Exception e) {
            logger.error("Error extracting role from JWT token: {}", e.getMessage());
            return null;
        }
    }

    // jwt 토큰이 유효한지 검사
    public boolean validateJwtToken(String authToken) {
        try {
            System.out.println("Validate");
            Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    // 비밀키 생성
    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }
}
