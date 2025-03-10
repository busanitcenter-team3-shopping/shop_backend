package com.secure.shopbackend.repositories;

import com.secure.shopbackend.dtos.Purchase;
import com.secure.shopbackend.dtos.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PurchaseRepository extends JpaRepository<Purchase, Integer> {
    // 특정 유저의 구매 내역 조회
    List<Purchase> findAllByUser(User user);

    // 특정 유저와 상품으로 구매 목록 찾기
    Purchase findByUserAndProduct_ProductId(User user, Long productId);
}