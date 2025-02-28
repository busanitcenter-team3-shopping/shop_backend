package com.secure.shopbackend.controller;

import com.secure.shopbackend.dtos.User;
import com.secure.shopbackend.repositories.UserRepository;
import com.secure.shopbackend.security.jwt.JwtUtils;
import com.secure.shopbackend.security.request.LoginRequest;
import com.secure.shopbackend.security.response.LoginResponse;
import com.secure.shopbackend.security.services.UserDetailsImpl;
import com.secure.shopbackend.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
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

    // 유저정보 가져오기
    @GetMapping
    public ResponseEntity<?> getUserDetails(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            System.out.println("userDetails is null");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
        }

        String email = userDetails.getEmail();

        Optional<User> user = userRepository.findByEmail(email);

        return ResponseEntity.ok(user);
    }

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

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        return ResponseEntity.ok(user);
    }

}
