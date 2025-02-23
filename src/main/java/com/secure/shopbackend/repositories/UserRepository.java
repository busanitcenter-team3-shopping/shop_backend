package com.secure.shopbackend.repositories;

import com.secure.shopbackend.dtos.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // 이메일 조회
    Optional<User> findByEmail(String email);


}
