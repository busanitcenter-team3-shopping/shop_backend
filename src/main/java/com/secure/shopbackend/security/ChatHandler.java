package com.secure.shopbackend.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
  private final Map<Long, Long> userChatRooms = new ConcurrentHashMap<>();
  private final Map<Long, List<String>> offlineMessages = new ConcurrentHashMap<>();

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
      // 유저가 현재 채팅방에 있는지 확인
      boolean isReceiverInRoom = userChatRooms.containsKey(receiverId)
              && userChatRooms.get(receiverId) != null
              && userChatRooms.get(receiverId).equals(chatRoomId);

      User sender = userRepository.findById(senderId).orElseThrow(()-> new RuntimeException("Sender not found"));
      User receiver = userRepository.findById(receiverId).orElseThrow(()-> new RuntimeException("Receiver not found"));

        ChatMessage chatMessage = new ChatMessage();
            chatMessage.setSender(sender);
            chatMessage.setReceiver(receiver);
            chatMessage.setContent(content);
            chatMessage.setChatRoom(chatRoomRepository.findById(chatRoomId).orElseThrow(()-> new RuntimeException("ChatRoom not found")));
            chatMessage.setTimestamp(LocalDateTime.now());
            chatMessage.setIsRead(isReceiverInRoom);

            ChatMessage savedMessage = chatService.saveMessage(chatMessage);

      int receiverUnreadCount = chatService.getUnreadAllMessagesCount(receiverId);

      ObjectNode response = objectMapper.createObjectNode();
      response.putPOJO("message", ChatMessage.fromEntity(savedMessage));
      response.put("unreadCount", receiverUnreadCount);
      response.put("chatRoomId", savedMessage.getChatRoom().getChatRoomId());

      log.info("📩 메시지 전송 - ID: {}, isRead: {}", savedMessage.getMessageId(), savedMessage.getIsRead());

//      ChatMessage responseMessage = ChatMessage.fromEntity(savedMessage);
      String jsonMessage = objectMapper.writeValueAsString(response);

      log.info("📩 WebSocket 메시지 전송 - 대상 유저 ID: {}, 내용: {}", receiverId, jsonMessage);

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
    Long chatRoomId = chatRoomIdStr.equals("global") ? null : Long.parseLong(chatRoomIdStr);

    log.info("✅ WebSocket 연결 성공 - 유저 ID: {}, 채팅방 ID: {}", userId, chatRoomId);

    if (userSessions.containsKey(userId)) {
      userSessions.get(userId).close();
    }
    userSessions.put(userId, session);

    if (chatRoomId != null) {
      userChatRooms.put(userId, chatRoomId);
      chatService.setUserChatRoom(userId, chatRoomId);  // ✅ chatRoomId가 null이 아닐 때만 실행
      chatService.markMessagesAsRead(userId, chatRoomId);
    } else {
      userChatRooms.remove(userId);  // global이면 채팅방 정보 삭제
      log.info("🌍 유저 {}는 global 상태입니다.", userId);
    }

    log.info("✅ WebSocket 세션 저장 완료 - userSessions: {}", userSessions.keySet());

    if (offlineMessages.containsKey(userId)) {
      List<String> messages = new ArrayList<>(offlineMessages.get(userId)); // 리스트 복사
      offlineMessages.remove(userId); // 삭제
      for (String msg : messages) {
        session.sendMessage(new TextMessage(msg));
      }
      log.info("📩 유저 {}에게 저장된 오프라인 메시지 전송 완료", userId);
    }


  } catch (NumberFormatException e) {
    log.error("❌ 변환 오류: userId={}", userIdStr);
    session.close();
  }
}


  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    Long userId = null;

    // 🚀 userSessions에서 해당 세션을 가진 userId 찾기
    for (Map.Entry<Long, WebSocketSession> entry : userSessions.entrySet()) {
      if (entry.getValue().equals(session)) {
        userId = entry.getKey();
        break;
      }
    }

    if (userId != null) {
      userSessions.remove(userId);
      userChatRooms.remove(userId); // 유저가 채팅방을 나가면 상태 제거
      log.info("❌ WebSocket 연결 종료 - 유저 ID: {}", userId);
    }
  }

  private Map<String, String> getQueryParams(WebSocketSession session) {
    String query = session.getUri().getQuery();
    if (query == null) return Collections.emptyMap();
    System.out.println(query);

    return Arrays.stream(query.split("&"))
            .map(param -> param.split("=", 2))
            .collect(Collectors.toMap(
                    pair -> pair[0], // key
                    pair -> pair.length > 1 && !pair[1].isEmpty() ? pair[1] : null // value가 없으면 빈 문자열로 처리
            ));
  }

  public void sendMessageToBothUsers(Long senderId, Long receiverId, String message) throws Exception {
    WebSocketSession senderSession = userSessions.get(senderId);
    WebSocketSession receiverSession = userSessions.get(receiverId);

    if (receiverSession != null && receiverSession.isOpen()) {
      receiverSession.sendMessage(new TextMessage(message));
      log.info("📩 메시지 전송 완료 → 수신자 ID: {}", receiverId);
    } else {
      log.warn("⚠ 받은 사람 세션 없음 - 오프라인 메시지 저장 (ID: {})", receiverId);
      saveOfflineMessage(receiverId, message);
    }
  }

  private void saveOfflineMessage(Long receiverId, String message) {
    offlineMessages.computeIfAbsent(receiverId, k -> new ArrayList<>()).add(message);
    log.info("📩 받은 사람({})이 오프라인 상태 - 메시지 저장됨", receiverId);
  }

  private Long getUserIdFromSession(WebSocketSession session) {
    Object userId = session.getAttributes().get("userId");
    if (userId instanceof Long) {
      return (Long) userId;
    }
    return null;
  }
}
