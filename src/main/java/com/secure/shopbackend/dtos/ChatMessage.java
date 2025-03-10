package com.secure.shopbackend.dtos;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "message")
public class ChatMessage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "message_id")
  private Long messageId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sender_id", nullable = true)
  private User sender;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "receiver_id", nullable = true)
  private User receiver;

  @Column(name = "content")
  private String content;

  @ManyToOne
  @JoinColumn(name = "chat_room_id", nullable = false)
  private ChatRoom chatRoom;

  @Column(name = "sent_time")
  private LocalDateTime timestamp;

  @Column(name = "is_read")
  private Boolean isRead;

  public static ChatMessage fromEntity(ChatMessage chatMessage) {
    return ChatMessage.builder()
            .messageId(chatMessage.getMessageId())
            .sender(chatMessage.getSender())
            .receiver(chatMessage.getReceiver())
            .content(chatMessage.getContent())
            .chatRoom(chatMessage.getChatRoom())
            .timestamp(chatMessage.getTimestamp())
            .isRead(chatMessage.getIsRead())
            .build();
  }
}
