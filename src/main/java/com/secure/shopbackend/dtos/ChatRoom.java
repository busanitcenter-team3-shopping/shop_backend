package com.secure.shopbackend.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "chat_room")
@Builder
@AllArgsConstructor
@NoArgsConstructor
//@JsonIgnoreProperties({"product"})
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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user1_id", nullable = false)
  private User user1;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user2_id", nullable = false)
  private User user2;

  public boolean isSameRoom(User u1, User u2, Product product) {
    return (user1.equals(u1) && user2.equals(u2)) || (user1.equals(u2) && user2.equals(u1));
  }

}
