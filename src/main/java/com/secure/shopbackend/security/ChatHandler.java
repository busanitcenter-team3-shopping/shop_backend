package com.secure.shopbackend.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.secure.shopbackend.dtos.ChatMessage;
import com.secure.shopbackend.dtos.ChatRoom;
import com.secure.shopbackend.dtos.User;
import com.secure.shopbackend.repositories.ChatRoomRepository;
import com.secure.shopbackend.repositories.UserRepository;
import com.secure.shopbackend.services.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ChatHandler extends TextWebSocketHandler {

  private static Map<Long, WebSocketSession> userSessions = new ConcurrentHashMap<>();

  private final ChatService chatService;

  private final ObjectMapper objectMapper;
  private final UserRepository userRepository;
  private final ChatRoomRepository chatRoomRepository;
  private final SimpleControllerHandlerAdapter simpleControllerHandlerAdapter;

  public ChatHandler(ChatService chatService, ObjectMapper objectMapper, UserRepository userRepository, ChatRoomRepository chatRoomRepository, SimpleControllerHandlerAdapter simpleControllerHandlerAdapter) {
    this.chatService = chatService;
    this.objectMapper = objectMapper;
    this.userRepository = userRepository;
    this.chatRoomRepository = chatRoomRepository;
    this.simpleControllerHandlerAdapter = simpleControllerHandlerAdapter;
  }


  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    String payload = message.getPayload();
    log.info("payload:" + payload);

    try {
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.registerModule(new JavaTimeModule());
      JsonNode jsonNode = objectMapper.readTree(payload);

      Long senderId = jsonNode.get("senderId").asLong();
      Long receiverId = jsonNode.get("receiverId").asLong();
      Long chatRoomId = jsonNode.get("chatRoomId").asLong();
      String content = jsonNode.get("content").asText();

      if (senderId == null || chatRoomId == null || receiverId == null) {
        log.error("❌ 메시지 전송 오류: senderId 또는 chatRoomId 또는 receiverId 가 null입니다.");
        return;
      }

      User sender = userRepository.findById(senderId).orElseThrow(()-> new RuntimeException("Sender not found"));
      User receiver = userRepository.findById(receiverId).orElseThrow(()-> new RuntimeException("Receiver not found"));

        ChatMessage chatMessage = new ChatMessage();
            chatMessage.setSender(sender);
            chatMessage.setReceiver(receiver);
            chatMessage.setContent(content);
            chatMessage.setChatRoom(chatRoomRepository.findById(chatRoomId).orElseThrow(()-> new RuntimeException("ChatRoom not found")));
            chatMessage.setTimestamp(LocalDateTime.now());
            chatMessage.setIsRead(false);

            ChatMessage savedMessage = chatService.saveMessage(chatMessage);


      ChatMessage responseMessage = ChatMessage.fromEntity(savedMessage);
      String jsonMessage = objectMapper.writeValueAsString(responseMessage);

      sendMessageToBothUsers( savedMessage.getSender().getUserId(), savedMessage.getReceiver().getUserId(), jsonMessage);
//      sendMessageToUser(savedMessage.getSender().getUserId(), jsonMessage);

    } catch (Exception e) {
      log.error(e.getMessage());
    }
  }

@Override
public void afterConnectionEstablished(WebSocketSession session) throws Exception {
  Map<String, String> params = getQueryParams(session);
  String userIdStr = params.get("userId");
  String chatRoomIdStr = params.get("chatRoomId"); // chatRoomId도 가져오기

  if (userIdStr == null || userIdStr.isEmpty() || userIdStr.equals("undefined") || chatRoomIdStr == null || chatRoomIdStr.isEmpty()) {
    log.error("❌ WebSocket 연결 오류: userId 또는 chatRoomId가 올바르지 않습니다. (userId: {}, chatRoomId: {})", userIdStr, chatRoomIdStr);
    session.close();
    return;
  }

  try {
    Long userId = Long.parseLong(userIdStr);
    Long chatRoomId = Long.parseLong(chatRoomIdStr); // chatRoomId 파싱

    log.info("✅ 채팅방 {} 연결 성공", userId);

    // chatRoomId와 userId를 모두 전달
    chatService.markMessagesAsRead(userId, chatRoomId);

    userSessions.put(userId, session);
  } catch (NumberFormatException e) {
    log.error("❌ 변환 오류: userId={}, chatRoomId={}", userIdStr, chatRoomIdStr);
    session.close();
  }
}




  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    userSessions.values().removeIf(s -> s.equals(session));
    log.info(session + "클라이언트 접속 해제");
  }

  private Map<String, String> getQueryParams(WebSocketSession session) {
    String query = session.getUri().getQuery();
    if (query == null) return Collections.emptyMap();

    return Arrays.stream(query.split("&"))
            .map(param -> param.split("="))
            .collect(Collectors.toMap(pair->pair[0],pair->pair[1]));
  }

  public void sendMessageToBothUsers(Long senderId, Long receiverId, String message) throws Exception {
    WebSocketSession senderSession = userSessions.get(senderId);
    WebSocketSession receiverSession = userSessions.get(receiverId);

//    if (senderSession != null && senderSession.isOpen()) {
//      senderSession.sendMessage(new TextMessage(message));
//      log.info("📩 메시지 전송됨 → 발신자 ID: {}", senderId);
//    } else {
//      log.warn("⚠ WebSocket 세션 없음 - 발신자 ID: {}", senderId);
//    }

    if (receiverSession != null && receiverSession.isOpen()) {
      receiverSession.sendMessage(new TextMessage(message));
      log.info("📩 받은 사람에게 메시지 전송 완료 → ID: {}", receiverId);
    } else {
      log.warn("⚠ 받은 사람 세션 없음 - ID: {}", receiverId);
    }
  }
}
