package com.secure.shopbackend.dtos;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime created_at;

    @UpdateTimestamp
    private LocalDateTime updated_at;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;


    @ManyToOne(optional = true)
    @JoinColumn(name = "image_id")
    private Image image;


    @ManyToOne
    @JoinColumn(name = "purchase_id")
    private Purchase purchase;

    //review_type : 리뷰 타입(구매자>판매자, 판매자>구매자) 구분용
    @Column(name = "review_type")
    private String reviewType;

}
