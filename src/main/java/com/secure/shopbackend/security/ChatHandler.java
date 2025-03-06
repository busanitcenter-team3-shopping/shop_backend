package com.secure.shopbackend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.secure.shopbackend.dtos.ChatMessage;
import com.secure.shopbackend.dtos.ChatRoom;
import com.secure.shopbackend.dtos.User;
import com.secure.shopbackend.repositories.UserRepository;
import com.secure.shopbackend.services.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

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

  public ChatHandler(ChatService chatService, ObjectMapper objectMapper, UserRepository userRepository) {
    this.chatService = chatService;
    this.objectMapper = objectMapper;
    this.userRepository = userRepository;
  }

  
  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    String payload = message.getPayload();
    log.info("payload:" + payload);

    try {
      ChatMessage chatMessage = objectMapper.readValue(payload, ChatMessage.class);

      ChatMessage savedMessage = chatService.saveMessage(
              chatMessage.getSender().getUserId(),
              chatMessage.getReceiver().getUserId(),
              chatMessage.getContent(),
              chatMessage.getChatRoom().getChatRoomId()
      );

      ChatMessage responseMessage = ChatMessage.fromEntity(savedMessage);
      String jsonMessage = objectMapper.writeValueAsString(responseMessage);

      sendMessageToUser(savedMessage.getReceiver().getUserId(), jsonMessage);
      sendMessageToUser(savedMessage.getSender().getUserId(), jsonMessage);

    } catch (Exception e) {
      log.error(e.getMessage());
    }
  }
  
  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    Map<String, String> params = getQueryParams(session);
    String userIdStr = params.get("userId");

    if (userIdStr == null || userIdStr.isEmpty() || userIdStr.equals("undefined")) {
      log.error("❌ WebSocket 연결 오류: userId가 올바르지 않습니다. (roomId: {})", userIdStr);
      session.close();
      return;
    }

    try {
      Long userId = Long.parseLong(userIdStr);
      log.info("✅ 채팅방 {} 연결 성공", userId);

      userSessions.put(userId, session);
    } catch (NumberFormatException e) {
      log.error("❌ roomId 변환 오류: {}", userIdStr);
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

  public void sendMessageToUser(Long userId, String message) throws Exception {
    WebSocketSession session = userSessions.get(userId);
    if (session != null && session.isOpen()) {
      session.sendMessage(new TextMessage(message));
      log.info("📩 메시지 전송됨 → 수신자 ID: {}", userId);
    } else {
      log.warn("⚠ WebSocket 세션 없음 - 수신자 ID: {}", userId);
    }
  }
}
