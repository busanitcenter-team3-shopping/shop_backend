package com.secure.shopbackend.controller;

import com.secure.shopbackend.dtos.ChatMessage;
import com.secure.shopbackend.dtos.ChatRoom;
import com.secure.shopbackend.security.services.UserDetailsImpl;
import com.secure.shopbackend.services.ChatService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Log4j2
@RequestMapping("/chat")
public class ChatController {

  @Autowired
  private ChatService chatService;

//  @GetMapping("/chat")
//  public String chatGET() {
//    log.info("@ChatController, chat GET()");
//    return "chater";
//  }

  @GetMapping("/rooms/{id}/messages")
  public List<ChatMessage> getMessagesByRoom(@PathVariable Long id) {
    return chatService.getMessagesByChatRoomId(id);
  }

  @PostMapping("/rooms")
  public ResponseEntity<ChatRoom> createRoom(@RequestParam String status,
                                             @RequestParam Long productId,
                                             @RequestParam Long userId) {
    ChatRoom newRoom = chatService.createChatRoom(status, productId, userId);

    return ResponseEntity.ok(newRoom);
  }

  @GetMapping("/rooms")
  public List<ChatRoom> getMyChatRooms(@AuthenticationPrincipal UserDetailsImpl userDetails) {
    return chatService.getMyChatRooms(userDetails);
  }

  @GetMapping("/rooms/{id}")
  public ChatRoom getRoomById(@PathVariable Long id){
    return chatService.getChatRoomById(id);
  }

  @GetMapping("/rooms/{id}/details")
  public ResponseEntity<ChatRoom> getRoomDetails(@PathVariable Long id){
    ChatRoom room = chatService.getChatRoomDetails(id);
    return ResponseEntity.ok(room);
  }
}
