package com.secure.shopbackend.repositories;

import com.secure.shopbackend.dtos.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {


}
