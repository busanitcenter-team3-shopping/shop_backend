package com.secure.shopbackend.repositories;

import com.secure.shopbackend.dtos.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    // 관리자 이메일로 찾기
    Optional<Admin> findByEmail(String email);
}
