package com.secure.shopbackend.repositories;

import com.secure.shopbackend.dtos.User;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // 이메일 조회
    Optional<User> findByEmail(String email);
    
    // 유저이름 조회
    Optional<User> findByUsername(String username);

    Long findByUserId(Long userId);

    String email(@NotBlank String email);
}
