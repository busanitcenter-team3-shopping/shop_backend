package com.secure.shopbackend.services;

import com.secure.shopbackend.dtos.Product;
import com.secure.shopbackend.dtos.User;
import com.secure.shopbackend.repositories.ProductRepository;
import com.secure.shopbackend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
  @Autowired
  private ProductRepository productRepository;

    // 회원가입
    public User createUser(User user) {
        User createdUser = new User();
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        } else {
            createdUser.setUsername(user.getUsername());
            createdUser.setEmail(user.getEmail());
//            createdUser.setPassword(user.getPassword());
            createdUser.setPassword(passwordEncoder.encode(user.getPassword()));
            createdUser.setPhone(user.getPhone());
            createdUser.setCreated_at(LocalDateTime.now());

            userRepository.save(createdUser);
            return createdUser;
        }
    }

    // 전체회원 조회
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 회원 수정
    public User updateUser(Long userid, User newUser) {
        User user = userRepository.findById(userid)
                .orElseThrow(() -> new RuntimeException("유저를 찾을수 없습니다."));
        user.setUsername(newUser.getUsername());
        if (newUser.getPassword() != null && !newUser.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(newUser.getPassword()));
        }
        user.setPhone(newUser.getPhone());
        user.setUpdated_at(LocalDateTime.now());

        return userRepository.save(user);
    }

    // 회원 삭제
    public void deleteUser(Long userid) {
        if(!userRepository.existsById(userid)) {
            throw new RuntimeException("유저를 찾을수 없습니다.");
        }


        userRepository.deleteById(userid);
    }
}
