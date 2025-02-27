package com.secure.shopbackend.controller;

import com.secure.shopbackend.dtos.Admin;
import com.secure.shopbackend.repositories.AdminRepository;
import com.secure.shopbackend.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("/login")
public class CombinedLoginController {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping
    public ResponseEntity<?> login(@RequestBody com.secure.shopbackend.security.request.LoginRequest loginRequest) {
        // 1. 관리자 로그인 시도: email 기준으로 관리자 테이블에서 검색
        Optional<Admin> adminOpt = adminRepository.findByEmail(loginRequest.getEmail());
        if (adminOpt.isPresent()) {
            com.secure.shopbackend.dtos.Admin admin = adminOpt.get();
            if (!loginRequest.getPassword().equals(admin.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("비밀번호가 틀렸습니다.");
            }
            String token = jwtUtils.generateTokenFromUsername(admin);
            Map<String, Object> response = new HashMap<>();
            response.put("jwtToken", token);
            response.put("email", admin.getEmail());
            response.put("role", "ROLE_ADMIN");
            return ResponseEntity.ok(response);
        }
        // 2. 관리자 계정이 아니면 일반 유저 로그인 시도
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // 인증된 유저 정보 가져오기
            org.springframework.security.core.userdetails.UserDetails userDetails =
                    (org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal();
            String jwtToken = jwtUtils.generateTokenFromUsername(userDetails);
            Map<String, Object> response = new HashMap<>();
            response.put("jwtToken", jwtToken);
            response.put("email", loginRequest.getEmail());
            response.put("role", "ROLE_USER");
            return ResponseEntity.ok(response);
        } catch (AuthenticationException exception) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Bad credentials");
            map.put("status", false);
            return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);
        }
    }
}

