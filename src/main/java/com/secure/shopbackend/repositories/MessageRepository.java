package com.secure.shopbackend.repositories;

import com.secure.shopbackend.dtos.ChatMessage;
import com.secure.shopbackend.dtos.ChatRoom;
import jakarta.transaction.Transactional;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoom(ChatRoom chatRoom);

    @Transactional
    @Modifying
    @Query("UPDATE ChatMessage SET isRead = true WHERE receiver.userId = :userId AND isRead = false ")
    int markMessagesAsRead(Long userId);

    // 채팅방 하나의 읽지않은 메서드 수
    @Query("SELECT COUNT(*) FROM ChatMessage WHERE chatRoom.chatRoomId = :chatRoomId AND receiver.userId = :userId AND isRead = false")
    int countRead(@Param("chatRoomId")Long chatRoomId, @Param("userId") Long userId);
    
    
    // 채팅방 전체 읽지않은 메서드 수
    @Query("SELECT COUNT(m) FROM ChatMessage m WHERE m.receiver.userId = :userId AND m.isRead = false")
    int countUnreadALLMessages(@Param("userId") Long userId);
}
