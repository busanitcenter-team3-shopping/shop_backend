package com.secure.shopbackend.services;

import com.secure.shopbackend.dtos.ChatRoom;
import com.secure.shopbackend.dtos.Product;
import com.secure.shopbackend.dtos.Purchase;
import com.secure.shopbackend.dtos.User;
import com.secure.shopbackend.repositories.ChatRoomRepository;
import com.secure.shopbackend.repositories.ProductRepository;
import com.secure.shopbackend.repositories.PurchaseRepository;
import com.secure.shopbackend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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

    // 상품 판매
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
}
