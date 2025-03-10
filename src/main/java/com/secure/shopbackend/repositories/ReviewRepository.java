package com.secure.shopbackend.repositories;

import com.secure.shopbackend.dtos.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

}
