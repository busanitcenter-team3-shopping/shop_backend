package com.secure.shopbackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



//postman에서 리뷰 내용 깔끔하게 볼려고 임시로 만든거입니다.
// 이거 그대로 프론트에 사용해도 괜찮을듯 합니다. 필요한 내용만 딱 가져오는거라서
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SellerReviewResponseDto {
    private Long reviewerId;
    private String reviewerName;
    private String reviewTitle;
    private String reviewContent;
    private Long productId;
    private String productTitle;
    private String image;
}
