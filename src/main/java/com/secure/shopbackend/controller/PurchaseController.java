package com.secure.shopbackend.controller;

import com.secure.shopbackend.dtos.Purchase;
import com.secure.shopbackend.dtos.PurchaseResponseDto;
import com.secure.shopbackend.dtos.User;
import com.secure.shopbackend.repositories.PurchaseRepository;
import com.secure.shopbackend.repositories.UserRepository;
import com.secure.shopbackend.security.services.UserDetailsImpl;
import com.secure.shopbackend.services.PurchaseService;
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


    @Autowired
    private PurchaseService purchaseService;

//    @GetMapping
//    public ResponseEntity<?> getPurchases(@AuthenticationPrincipal UserDetailsImpl userDetails) {
//        Long userId = userDetails.getId();
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//        // 리뷰까지 함께 보여줌(글쓰기 여부)
//        List<Purchase> purchase = purchaseRepository.findAllByUser(user);
//        return ResponseEntity.ok(purchase);
//    }


    @GetMapping
    public ResponseEntity<?> getPurchases(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getId();
        // purchaseService 사용 가능
        List<PurchaseResponseDto> dtoList = purchaseService.getPurchasesForUser(userId);
        return ResponseEntity.ok(dtoList);
    }

    // 모든 판매내역 조회
    @GetMapping("/all")
    public ResponseEntity<?> getAllPurchases() {
        List<Purchase> purchases = purchaseRepository.findAll();
        return ResponseEntity.ok(purchases);
    }
}



