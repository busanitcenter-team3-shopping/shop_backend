package com.secure.shopbackend.services;

import com.secure.shopbackend.dtos.*;
import com.secure.shopbackend.repositories.*;
import com.secure.shopbackend.security.services.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final ChatParticipantRepository chatParticipantRepository;

    // 1:1 채팅방 찾기 (없으면 자동 생성)
    @Transactional
    public ChatRoom findOrCreateChatRoom(Long user1Id, Long user2Id, Long productId) {
        User user1 = userRepository.findById(user1Id)
                .orElseThrow(() -> new RuntimeException("사용자 1 찾을 수 없음"));
        User user2 = userRepository.findById(user2Id)
                .orElseThrow(() -> new RuntimeException("사용자 2 찾을 수 없음"));
        Product product = productRepository.findById(productId).orElseThrow(()-> new RuntimeException("상품을 찾을 수 없음"));

        // 기존 채팅방이 있는지 확인
        Optional<ChatRoom> existingRoom = chatRoomRepository.findAll()
                .stream()
                .filter(room -> room.isSameRoom(user1, user2, product))
                .findFirst();

        return existingRoom.orElseGet(() -> {
            ChatRoom newRoom = ChatRoom.builder()
                    .user1(user1)
                    .user2(user2)
                    .build();
            return chatRoomRepository.save(newRoom);
        });
    }

    //메시지 저장
    public ChatMessage saveMessage (ChatMessage chatMessage) {

       return messageRepository.save(chatMessage);
    }
    
    //메시지 리스트 조회
    public List<ChatMessage> getMessages() {
        return messageRepository.findAll();
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
                .filter(room -> room.getUser1().getUserId().equals(user.getUserId())|| room.getUser2().getUserId().equals(user.getUserId()))
                .collect(Collectors.toList());
    }

    public ChatRoom getChatRoomById(Long chatRoomId){
        return chatRoomRepository.findById(chatRoomId).orElseThrow(()->  new RuntimeException("ChatRoom not found"));
    }

    public List<ChatMessage> getMessagesByChatRoomId(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(()-> new RuntimeException("ChatRoom not found"));

        return messageRepository.findByChatRoom(chatRoom);
    }

    public void addParticipant(Long userId, Long chatRoomId){
        User user = userRepository.findById(userId).orElseThrow(()-> new RuntimeException("User not found"));
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(()-> new RuntimeException("ChatRoom not found"));

        boolean alreadyExists = chatParticipantRepository.findByUserAndChatRoom(user, chatRoom).isPresent();

        if(alreadyExists){
            ChatParticipant participant = new ChatParticipant(user, chatRoom);
            chatParticipantRepository.save(participant);
            System.out.println("✅ 사용자 " + user.getUsername() + "가 채팅방 " + chatRoomId + "에 참여했습니다.");

        }
    }

    public ChatRoom getChatRoomDetails(Long id) {
        ChatRoom chatRoom = chatRoomRepository.findById(id).orElseThrow(()-> new RuntimeException("ChatRoom not found"));

        return chatRoom;
    }
}
