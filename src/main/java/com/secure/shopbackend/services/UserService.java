package com.secure.shopbackend.services;

import com.secure.shopbackend.dtos.User;
import com.secure.shopbackend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService{

    @Autowired
    private UserRepository userRepository;


    // 회원가입
    public User createUser(User user) {
        User createdUser = new User();
        createdUser.setName(user.getName());
        createdUser.setEmail(user.getEmail());
        createdUser.setPassword(user.getPassword());
        createdUser.setPhone(user.getPhone());
        createdUser.setCreated_at(LocalDateTime.now());

        userRepository.save(createdUser);
        return createdUser;
    }

    // 전체회원 조회
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 회원 수정
    public User updateUser(Long userid, String password, String phone, String name) {
        User user = userRepository.findById(userid)
                .orElseThrow(() -> new RuntimeException("유저를 찾을수 없습니다."));
        user.setName(name);
        user.setPassword(password);
        user.setPhone(phone);
        user.setUpdated_at(LocalDateTime.now());

        return userRepository.save(user);
    }

    // 회원 삭제
    public void deleteUser(Long userid) {
        userRepository.deleteById(userid);
    }
}
