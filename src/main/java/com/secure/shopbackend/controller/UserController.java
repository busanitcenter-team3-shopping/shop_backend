package com.secure.shopbackend.controller;

import com.secure.shopbackend.dtos.User;
import com.secure.shopbackend.repositories.UserRepository;
import com.secure.shopbackend.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("*")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

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
}
