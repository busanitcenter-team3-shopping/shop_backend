package com.secure.shopbackend.repositories;

import com.secure.shopbackend.dtos.Image;
import com.secure.shopbackend.dtos.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
// 이미지 삭제
  void deleteByProduct(Product product);

  List<Image> findByProduct_ProductId(Long product_productId);
}

