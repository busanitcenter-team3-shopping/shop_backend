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

    @PostMapping("/createuser")
    public ResponseEntity<?> createUser(@Valid @RequestBody User user, BindingResult bindingResult) {

        // dto message에 남긴 말들
        if(bindingResult.hasErrors()) {
            String error = bindingResult.getFieldError().getDefaultMessage();
            return ResponseEntity.badRequest().body(error);
        }

        // 이메일 중복
        if(userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("동일한 이메일이 존재합니다.");
        }
        userService.createUser(user);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/updateuser/{id}")
    public User updateUser(@RequestBody User newUser, @PathVariable Long id, String password, String phone, String name) {
        Optional<User> user = userRepository.findById(id);
        return userService.updateUser(id, password, phone, name);
    }

    @GetMapping("/userlist")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @DeleteMapping("/delete/{id}")
    public void deleteUser(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        userService.deleteUser(id);
    }
}
