package com.secure.shopbackend.repositories;

import com.secure.shopbackend.dtos.Category;
import com.secure.shopbackend.dtos.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface  ProductRepository extends JpaRepository<Product, Long> {
  Optional<Product> findById(Long id);

  List<Product> findByTitleContainingIgnoreCase(String search);

  List<Product> findByCategoryAndTitleContainingIgnoreCase(Category category, String search);
}
