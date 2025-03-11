package com.secure.shopbackend.services;

import com.secure.shopbackend.dtos.*;
import com.secure.shopbackend.repositories.ChatRoomRepository;
import com.secure.shopbackend.repositories.ProductRepository;
import com.secure.shopbackend.repositories.PurchaseRepository;
import com.secure.shopbackend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PurchaseService {

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private ProductRepository productRepository;

     //상품 판매
    public ResponseEntity<?> completePurchase(Long chatRoomId, Long buyerId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new RuntimeException("방을 찾을수 없습니다."));
        User buyer = userRepository.findById(buyerId).orElseThrow(() -> new RuntimeException("구매자를 찾을수 없습니다."));

        Product product = chatRoom.getProduct();

        product.setStatus("판매완료");
        productRepository.save(product);

        Purchase purchase = new Purchase();
        purchase.setProduct(product);
        purchase.setUser(buyer);
        purchaseRepository.save(purchase);

        return ResponseEntity.ok().build();

    }

    //DTO를 통해 필요한 정보 받아오게 설정하느라 추가했습니다.
    public List<PurchaseResponseDto> getPurchasesForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // purchase 엔티티 목록 조회
        List<Purchase> purchases = purchaseRepository.findAllByUser(user);

        // 엔티티 -> DTO 변환
        return purchases.stream().map(purchase -> {
            PurchaseResponseDto dto = new PurchaseResponseDto();
            dto.setPurchaseId(purchase.getPurchaseId());
            dto.setProduct(purchase.getProduct());
            dto.setUserId(purchase.getUser().getUserId());
            dto.setAlreadyReviewed(
                    purchase.getReviews() != null && !purchase.getReviews().isEmpty()
            );
            return dto;
        }).collect(Collectors.toList());
    }
    

}
