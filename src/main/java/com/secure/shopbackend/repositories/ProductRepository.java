package com.secure.shopbackend.repositories;

import com.secure.shopbackend.dtos.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface  ProductRepository extends JpaRepository<Product, Long> {

}
