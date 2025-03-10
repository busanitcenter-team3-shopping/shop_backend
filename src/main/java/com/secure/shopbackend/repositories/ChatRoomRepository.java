package com.secure.shopbackend.repositories;

import com.secure.shopbackend.dtos.ChatRoom;
import com.secure.shopbackend.dtos.Product;
import com.secure.shopbackend.dtos.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

  Optional<ChatRoom> findByUser1AndUser2AndProduct(User user1, User user2, Product product);

  Optional<ChatRoom> findByUser2AndUser1AndProduct(User user2, User user1, Product product);

  List<ChatRoom> findByUser1OrUser2(User user1, User user2);
}
