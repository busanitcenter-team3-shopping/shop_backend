package com.secure.shopbackend.dtos;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
@Entity
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long imageId;

    @NotBlank
    @Column(name = "image_name")
    private String imageName;

    @ManyToOne
    @JoinColumn(name = "product_id") // 외래 키 컬럼 설정
    private Product product;
}