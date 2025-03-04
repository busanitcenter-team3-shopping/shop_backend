package com.secure.shopbackend.repositories;

import com.secure.shopbackend.dtos.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
}
