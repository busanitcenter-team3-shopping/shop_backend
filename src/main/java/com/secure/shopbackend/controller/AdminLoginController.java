package com.secure.shopbackend.controller;

import com.secure.shopbackend.dtos.Admin;
import com.secure.shopbackend.repositories.AdminRepository;
import com.secure.shopbackend.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@CrossOrigin("*")
@RequestMapping("/admin")
public class AdminLoginController {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;  // SecurityConfig에서 BCryptPasswordEncoder Bean 등록되어 있음

    // 관리자 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Admin loginRequest) {
        try {
            Admin admin = adminRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("관리자를 찾을 수 없습니다."));
            if (!loginRequest.getPassword().equals(admin.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호가 틀렸습니다.");
            }
            String token = jwtUtils.generateTokenFromUsername(admin);
            Map<String, Object> response = new HashMap<>();
            response.put("jwtToken", token);
            response.put("email", admin.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("로그인 처리 중 오류 발생: " + e.getMessage());
        }
    }

}

