package com.secure.shopbackend.repositories;

import com.secure.shopbackend.dtos.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<ChatMessage, Long> {
}
