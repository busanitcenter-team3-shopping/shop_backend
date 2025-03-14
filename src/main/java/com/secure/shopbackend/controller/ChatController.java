package com.secure.shopbackend.controller;

import com.secure.shopbackend.dtos.ChatMessage;
import com.secure.shopbackend.dtos.ChatRoom;
import com.secure.shopbackend.repositories.PurchaseRepository;
import com.secure.shopbackend.security.services.UserDetailsImpl;
import com.secure.shopbackend.services.ChatService;
import com.secure.shopbackend.services.PurchaseService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Log4j2
@RequestMapping("/chat")
public class ChatController {

  @Autowired
  private ChatService chatService;

  @Autowired
  private PurchaseService purchaseService;

  // 채팅 메시지 불러오기
  @GetMapping("/rooms/{id}/messages")
  public List<Map<String, Object>> getMessagesByRoom(@PathVariable Long id) {
    List<ChatMessage> messages = chatService.getMessagesByChatRoomId(id);

    return messages.stream().map(msg -> {
      Map<String, Object> messageData = new HashMap<>();
      messageData.put("messageId", msg.getMessageId());
      messageData.put("chatRoomId", msg.getChatRoom().getChatRoomId());
      messageData.put("senderId", msg.getSender().getUserId()); // senderId 추가
      messageData.put("receiverId", msg.getReceiver().getUserId()); // receiverId 추가
      messageData.put("content", msg.getContent());
      messageData.put("timestamp", msg.getTimestamp());
      messageData.put("isRead", msg.getIsRead());
      return messageData;
    }).collect(Collectors.toList());
  }

  @PostMapping("/rooms")
  public ResponseEntity<ChatRoom> createRoom(@RequestParam String status,
                                             @RequestParam Long productId,
                                             @RequestParam Long userId) {
    ChatRoom newRoom = chatService.createChatRoom(status, productId, userId);

    return ResponseEntity.ok(newRoom);
  }

  // 나의 채팅방 목록
  @GetMapping("/rooms")
  public List<ChatRoom> getMyChatRooms(@AuthenticationPrincipal UserDetailsImpl userDetails) {
    return chatService.getMyChatRooms(userDetails);
  }

  // 채팅방
  @GetMapping("/rooms/{id}")
  public ChatRoom getRoomById(@PathVariable Long id){
    return chatService.getChatRoomById(id);
  }

  // 채팅방 정보
  @GetMapping("/rooms/{id}/details")
  public ResponseEntity<ChatRoom> getRoomDetails(@PathVariable Long id){
    ChatRoom room = chatService.getChatRoomDetails(id);
    return ResponseEntity.ok(room);
  }

  // 하나의 채팅방에 읽지않은 메시지 개수
  @GetMapping("/rooms/{id}/unread-count")
  public ResponseEntity<Integer> getUnreadMessageCount(@PathVariable Long id, @RequestParam Long userId) {
    int unreadCount = chatService.getUnreadMessageCount(id, userId);
    return ResponseEntity.ok(unreadCount);
  }


  //한 유저의 전체 채팅방 읽지 않은 메시지 개수
  @GetMapping("/rooms/unread")
  public ResponseEntity<Integer> getUnreadAllMessagesCount(@RequestParam Long userId) {
    int unreadCount = chatService.getUnreadAllMessagesCount(userId);
    return ResponseEntity.ok(unreadCount);

  }

  @PostMapping("/rooms/{chatRoomId}/mark-as-read")
  public ResponseEntity<?> markMessagesAsRead(@PathVariable Long chatRoomId, @RequestParam Long userId) {
    chatService.markMessagesAsRead(chatRoomId, userId);
    return ResponseEntity.ok().build();
  }

  //상품 판매
  @PostMapping("/purchase/{chatRoomId}")
  public ResponseEntity<?> completePurchase(
          @PathVariable Long chatRoomId,
          @RequestBody Map<String, Long> requestBody) {
    Long buyerId = requestBody.get("buyerId");
    return purchaseService.completePurchase(chatRoomId, buyerId);
  }

  @GetMapping("/rooms/{chatRoomId}/isUserInRoom")
  public ResponseEntity<Boolean> isUserInRoom(@PathVariable Long chatRoomId, @RequestParam Long userId) {
    boolean isInRoom = chatService.isUserInRoom(userId, chatRoomId);
    return ResponseEntity.ok(isInRoom);
  }
}
