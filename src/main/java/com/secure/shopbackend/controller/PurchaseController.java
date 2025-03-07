package com.secure.shopbackend.controller;

import com.secure.shopbackend.dtos.Purchase;
import com.secure.shopbackend.dtos.Product;
import com.secure.shopbackend.dtos.User;
import com.secure.shopbackend.repositories.ProductRepository;
import com.secure.shopbackend.repositories.PurchaseRepository;
import com.secure.shopbackend.repositories.UserRepository;
import com.secure.shopbackend.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    private ProductRepository productRepository;

    @PostMapping
    public ResponseEntity<?> addPurchase(@RequestBody PurchaseRequest request, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getId(); // 실제 userId를 얻음
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findById((long) request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));



        if (purchaseRepository.findByUserAndProduct_ProductId(user, request.getProductId()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 구매한 상품입니다.");
        }

        Purchase purchase = new Purchase();
        purchase.setUser(user);
        purchase.setProduct(product);
        purchaseRepository.save(purchase);
        return ResponseEntity.ok("Purchase added");
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> removePruchase(@PathVariable int productId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getId(); // 실제 userId를 얻음
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Purchase purchase = purchaseRepository.findByUserAndProduct_ProductId(user, productId);
        if (purchase != null) {
            purchaseRepository.delete(purchase);
            return ResponseEntity.ok("Pruchase removed");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pruchase not found");
        }
    }

    // 로그인한 사용자의 찜 목록 조회 (GET)
    @GetMapping
    public ResponseEntity<?> getPruchases(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getId(); // 실제 userId를 얻음
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Purchase> purchase = purchaseRepository.findAllByUser(user);
        return ResponseEntity.ok(purchase);
    }

    public static class PurchaseRequest {
        private int productId;
        public int getProductId() { return productId; }
        public void setProductId(int productId) { this.productId = productId; }
    }
}
