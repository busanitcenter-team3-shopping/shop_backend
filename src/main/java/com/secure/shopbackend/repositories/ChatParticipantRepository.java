package com.secure.shopbackend.repositories;

import com.secure.shopbackend.dtos.ChatParticipant;
import com.secure.shopbackend.dtos.ChatRoom;
import com.secure.shopbackend.dtos.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {
    List<ChatParticipant> findByChatRoom(ChatRoom chatRoom);
    Optional<ChatParticipant> findByUserAndChatRoom(User user, ChatRoom chatRoom);
}
