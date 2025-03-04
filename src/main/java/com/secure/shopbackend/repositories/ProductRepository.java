package com.secure.shopbackend.repositories;

import com.secure.shopbackend.dtos.Category;
import com.secure.shopbackend.dtos.Product;
import jakarta.transaction.Transactional;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface  ProductRepository extends JpaRepository<Product, Long> {
  List<Product> findByUser_UserId(Long userId);

  Optional<Product> findById(Long id);

  List<Product> findByTitleContainingIgnoreCase(String search);

  List<Product> findByCategoryAndTitleContainingIgnoreCase(Category category, String search);

  @Modifying
  @Transactional
  @Query("DELETE FROM Product WHERE productId = :productId")
  void deleteProductById(@Param("productId") Long productId);

}
