package com.secure.shopbackend.dtos;

import lombok.Data;
import java.util.List;

@Data
public class PurchaseResponseDto {
    private Long purchaseId;
    private Product product;
    private Long userId;
    private boolean alreadyReviewed; // 구 > 판
    private boolean sellerAlreadyReviewed; // 판 > 구

}
