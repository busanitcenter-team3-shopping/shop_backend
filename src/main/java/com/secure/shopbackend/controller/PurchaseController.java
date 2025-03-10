package com.secure.shopbackend.controller;

import com.secure.shopbackend.dtos.Purchase;
import com.secure.shopbackend.dtos.User;
import com.secure.shopbackend.repositories.PurchaseRepository;
import com.secure.shopbackend.repositories.UserRepository;
import com.secure.shopbackend.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/purchase")
@CrossOrigin("*")
public class PurchaseController {

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<?> getPurchases(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getId(); // 실제 userId를 얻음
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Purchase> purchase = purchaseRepository.findAllByUser(user);
        return ResponseEntity.ok(purchase);
    }

    // 모든 판매내역 조회
    @GetMapping("/all")
    public ResponseEntity<?> getAllPurchases() {
        List<Purchase> purchases = purchaseRepository.findAll();
        return ResponseEntity.ok(purchases);
    }
}
