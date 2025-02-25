package com.secure.shopbackend.repositories;

import com.secure.shopbackend.dtos.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

}

