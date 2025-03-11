package com.secure.shopbackend.services;

import com.secure.shopbackend.dtos.*;
import com.secure.shopbackend.repositories.*;
import com.secure.shopbackend.security.services.UserDetailsImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    //메시지 저장
    public ChatMessage saveMessage (ChatMessage chatMessage) {

        return messageRepository.save(chatMessage);
    }

    //채팅방 생성
    public ChatRoom createChatRoom(String status, Long productId, Long userId){
        Product product = productRepository.findById(productId).orElseThrow(()-> new RuntimeException("Product not found"));

        User user1 = userRepository.findByUserId(userId);
        User user2 = product.getUser();

        Optional<ChatRoom> existingRoom = chatRoomRepository.findByUser1AndUser2AndProduct(user1, user2, product);
        if (existingRoom.isEmpty()) {
            existingRoom = chatRoomRepository.findByUser2AndUser1AndProduct(user1, user2, product);
        }

        return existingRoom.orElseGet(()->{
            ChatRoom room = new ChatRoom();
            room.setName(product.getTitle());
            room.setStatus(status);
            room.setProduct(product);
            room.setUser1(user1);
            room.setUser2(product.getUser());
            return chatRoomRepository.save(room);
        });

    }

    //모든 채팅방 조회
    public List<ChatRoom> getMyChatRooms(UserDetailsImpl userDetails) {
        User user = userRepository.findByUserId(userDetails.getId());
        System.out.println(userDetails);
        System.out.println("로그인한 유저:"+ user);
        return chatRoomRepository.findAll().stream()
                .filter(room -> room.getUser1() != null && room.getUser1().getUserId().equals(user.getUserId())
                        || (room.getUser2() != null && room.getUser2().getUserId().equals(user.getUserId())))
                .collect(Collectors.toList());

    }

    public ChatRoom getChatRoomById(Long chatRoomId){
        return chatRoomRepository.findById(chatRoomId).orElseThrow(()->  new RuntimeException("ChatRoom not found"));
    }

    public List<ChatMessage> getMessagesByChatRoomId(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(()-> new RuntimeException("ChatRoom not found"));

        return messageRepository.findByChatRoom(chatRoom);
    }

    public ChatRoom getChatRoomDetails(Long id) {
        ChatRoom chatRoom = chatRoomRepository.findById(id).orElseThrow(()-> new RuntimeException("ChatRoom not found"));

        return chatRoom;
    }

    @Transactional
    public void markMessagesAsRead(Long chatRoomId) {
        int updatedCount = messageRepository.markMessagesAsRead(chatRoomId);
    }

    // 방 안읽은 메시지 개수
    public int getUnreadMessageCount(Long chatRoomId, Long userId) {
        return messageRepository.countRead(chatRoomId, userId);
    }
    
    // 전체 방 읽지 않은 메시지 개수
    public int getUnreadAllMessagesCount(Long userId) {
        return messageRepository.countUnreadALLMessages(userId);
    }
}
