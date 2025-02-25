package com.secure.shopbackend.security.jwt;

import com.secure.shopbackend.dtos.Admin;
import com.secure.shopbackend.repositories.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



//포스트맨 결과확인을 위한 관리자 토큰 발급용입니다.
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @GetMapping("/adminToken")
    public ResponseEntity<?> getAdminToken() {
        // DB에서 admin@1.1 이메일로 관리자 조회
        Admin admin = adminRepository.findByEmail("admin@1.1")
                .orElseThrow(() -> new RuntimeException("관리자를 찾을 수 없습니다."));

        // JWT 토큰 생성
        String token = jwtUtils.generateTokenFromUsername(admin);
        return ResponseEntity.ok(token);
    }
}
