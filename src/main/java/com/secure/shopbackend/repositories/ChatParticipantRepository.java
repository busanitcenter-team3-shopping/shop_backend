package com.secure.shopbackend.repositories;

import com.secure.shopbackend.dtos.ChatParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {
}
