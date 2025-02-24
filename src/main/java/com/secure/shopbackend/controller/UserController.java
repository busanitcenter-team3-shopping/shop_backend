package com.secure.shopbackend.controller;

import com.secure.shopbackend.dtos.User;
import com.secure.shopbackend.repositories.UserRepository;
import com.secure.shopbackend.security.jwt.JwtUtils;
import com.secure.shopbackend.security.request.LoginRequest;
import com.secure.shopbackend.security.response.LoginResponse;
import com.secure.shopbackend.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@CrossOrigin("*")
@RequestMapping("/user")
public class UserController {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

//    @Autowired
//    private UserDetailsImpl userDetails;

    // 회원가입
    @PostMapping("/createuser")
    public ResponseEntity<?> createUser(@Valid @RequestBody User user, BindingResult bindingResult) {

        // dto message에 남긴 말들
        if(bindingResult.hasErrors()) {
            String error = bindingResult.getFieldError().getDefaultMessage();
            return ResponseEntity.badRequest().body(error);
        }
        try {
            userService.createUser(user);
            return ResponseEntity.ok().build();
        }catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 회원 수정
    @PutMapping("/updateuser/{id}")
    public ResponseEntity<?> updateUser(@RequestBody User newUser, @PathVariable Long id) {
        try {
            User updatedUser = userService.updateUser(id, newUser);
            return ResponseEntity.ok().body(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 회원 조회
    @GetMapping("/userlist")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 회원 삭제
    @DeleteMapping("/delete/{id}")
    public void deleteUser(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        userService.deleteUser(id);
    }

    //로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication;
        try {
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        } catch (AuthenticationException exception) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Bad credentials");
            map.put("status", false);
            return new ResponseEntity<Object>(map, HttpStatus.UNAUTHORIZED);
        }

        // 시큐리티 인증됨
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 인증된 유저디테일 가져옴
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // 인증된 유저에 jwt 토큰 생성하기
        String jwtToken = jwtUtils.generateTokenFromUsername(userDetails);

        // 유저의 권한 리스트 가져오기
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        //유저이름 유저권한 jwt 토큰으로 새 객체를 만듬
        LoginResponse response = new LoginResponse(jwtToken, loginRequest.getEmail());

        // response body 로 JWT 토큰을 포함한 response 객체로 리턴
        return ResponseEntity.ok(response);
    }

}
