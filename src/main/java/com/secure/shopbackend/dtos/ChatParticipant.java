package com.secure.shopbackend.dtos;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chat_participant")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatParticipant {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne
  @JoinColumn(name = "chat_room_id", nullable = false)
  private ChatRoom chatRoom;

  public ChatParticipant(User user, ChatRoom chatRoom) {
    this.user = user;
    this.chatRoom = chatRoom;
  }
}
