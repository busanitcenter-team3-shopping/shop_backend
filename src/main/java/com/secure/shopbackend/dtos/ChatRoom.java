package com.secure.shopbackend.dtos;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
public class ChatRoom {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "chat_room_id")
  private Long chatRoomId;

  @Column(name = "name")
  private String name;

  @Column(name = "status")
  private String status;

  @CreationTimestamp
  @Column(name = "created_at")
  private LocalDateTime creationDate;
}
