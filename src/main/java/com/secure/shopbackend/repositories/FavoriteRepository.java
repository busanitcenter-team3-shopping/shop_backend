package com.secure.shopbackend.repositories;

import com.secure.shopbackend.dtos.Favorite;
import com.secure.shopbackend.dtos.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {
    // 특정 유저의 찜 목록 조회
    List<Favorite> findAllByUser(User user);

    // 특정 유저와 상품으로 찜 항목 찾기
    Favorite findByUserAndProduct_ProductId(User user, int productId);
}
