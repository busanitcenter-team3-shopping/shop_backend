package com.secure.shopbackend.security.jwt;

import com.secure.shopbackend.dtos.Admin;
import com.secure.shopbackend.dtos.User;
import com.secure.shopbackend.repositories.AdminRepository;
import com.secure.shopbackend.repositories.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminRepository adminRepository;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            String jwt = jwtUtils.getJwtFromHeader(request);

            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUserNameFromJwtToken(jwt);
                // 추가: 토큰에서 역할 정보를 추출 (메서드 구현 필요)
                String role = jwtUtils.getRoleFromJwtToken(jwt);

                if ("ROLE_ADMIN".equals(role)) {
                    // 관리자 토큰인 경우 AdminRepository를 사용
                    Admin admin = adminRepository.findByEmail(username)
                            .orElseThrow(() -> new RuntimeException("관리자를 찾을 수 없습니다."));
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            admin, null, admin.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    // 일반 사용자 토큰인 경우 기존 로직 수행
                    User user = userRepository.findByUsername(username)
                            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            user, null, Collections.emptyList()); // 권한 추가 가능
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }

        } catch (Exception e) {
            logger.error("JWT 인증 중 오류 발생: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
