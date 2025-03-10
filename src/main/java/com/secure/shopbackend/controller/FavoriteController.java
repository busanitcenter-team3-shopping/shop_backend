package com.secure.shopbackend.controller;

import com.secure.shopbackend.dtos.Favorite;
import com.secure.shopbackend.dtos.Product;
import com.secure.shopbackend.dtos.User;
import com.secure.shopbackend.repositories.FavoriteRepository;
import com.secure.shopbackend.repositories.ProductRepository;
import com.secure.shopbackend.repositories.UserRepository;
import com.secure.shopbackend.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favorite")
@CrossOrigin("*")
public class FavoriteController {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    // 찜 추가 (POST)
    @PostMapping
    public ResponseEntity<?> addFavorite(@RequestBody FavoriteRequest request, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getId(); // 실제 userId를 얻음
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));



        // 이미 찜되어 있으면 에러 또는 무시 처리
        if (favoriteRepository.findByUserAndProduct_ProductId(user, request.getProductId()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 찜한 상품입니다.");
        }

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setProduct(product);
        favoriteRepository.save(favorite);
        return ResponseEntity.ok("Favorite added");
    }

    // 찜 삭제 (DELETE)
    @DeleteMapping("/{productId}")
    public ResponseEntity<?> removeFavorite(@PathVariable long productId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getId(); // 실제 userId를 얻음
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Favorite favorite = favoriteRepository.findByUserAndProduct_ProductId(user, productId);
        if (favorite != null) {
            favoriteRepository.delete(favorite);
            return ResponseEntity.ok("Favorite removed");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Favorite not found");
        }
    }

    // 로그인한 사용자의 찜 목록 조회 (GET)
    @GetMapping
    public ResponseEntity<?> getFavorites(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getId(); // 실제 userId를 얻음
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Favorite> favorites = favoriteRepository.findAllByUser(user);
        return ResponseEntity.ok(favorites);
    }

    // DTO for 찜 추가 요청
    public static class FavoriteRequest {
        private long productId;

        public long getProductId() {
            return productId;
        }

        public void setProductId(long productId) {
            this.productId = productId;
        }
    }
}
