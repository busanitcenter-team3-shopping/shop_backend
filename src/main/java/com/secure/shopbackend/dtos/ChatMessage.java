package com.secure.shopbackend.dtos;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "message")
public class ChatMessage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "message_id")
  private Long messageId;

  @Column(name = "user_id")
  private Long userId;

  @Column(name = "content")
  private String content;

  @ManyToOne
  @JoinColumn(name = "chat_room_id", nullable = false)
  private ChatRoom chatRoom;

  @Column(name = "sent_time")
  private LocalDateTime timestamp;

  @Column(name = "is_read")
  private Boolean isRead;
}
