package com.secure.shopbackend.controller;

import com.secure.shopbackend.dtos.Review;
import com.secure.shopbackend.dtos.SellerReviewResponseDto;
import com.secure.shopbackend.services.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    // 이미지가 없는 경우 josn 방식
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createReviewJson(
            @RequestBody @Valid Review reviewDto,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            String error = bindingResult.getFieldError().getDefaultMessage();
            return ResponseEntity.badRequest().body(error);
        }
        try {
            // 이미지 파일이 없으므로 null 전달
            Review createdReview = reviewService.createReview(reviewDto, null);
            return ResponseEntity.ok(createdReview);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("리뷰 등록 중 오류 발생: " + e.getMessage());
        }
    }

    // 이미지 첨부시 multipart 방식
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createReviewMultipart(
            @RequestPart("review") @Valid Review reviewDto,
            BindingResult bindingResult,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {

        if (bindingResult.hasErrors()) {
            String error = bindingResult.getFieldError().getDefaultMessage();
            return ResponseEntity.badRequest().body(error);
        }
        try {
            Review createdReview = reviewService.createReview(reviewDto, imageFile);
            return ResponseEntity.ok(createdReview);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("리뷰 등록 중 오류 발생: " + e.getMessage());
        }
    }

    // 리뷰 수정
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateReview(@RequestBody Review review, @PathVariable Long id) {
        try {
            Review updatedReview = reviewService.updateReview(id, review);
            return ResponseEntity.ok(updatedReview);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 리뷰 삭제
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable Long id) {
        try {
            reviewService.deleteReview(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/for/{userId}")
    public ResponseEntity<?> getReviewsForUser(@PathVariable Long userId) {
        try {
            List<SellerReviewResponseDto> dtos = reviewService.getReviewsForTarget(userId);
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}



