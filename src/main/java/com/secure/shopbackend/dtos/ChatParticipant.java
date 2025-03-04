package com.secure.shopbackend.dtos;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "chat_participant")
@Data
public class ChatParticipant {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @ManyToOne
  @JoinColumn(name = "chat_room_id", nullable = false)
  private ChatRoom chatRoom;
}
