package com.secure.shopbackend.services;

import com.secure.shopbackend.dtos.*;
import com.secure.shopbackend.repositories.PurchaseRepository;
import com.secure.shopbackend.repositories.ReviewRepository;
import com.secure.shopbackend.repositories.ReviewImageRepository;  // 추가
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private ReviewImageRepository reviewImageRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Transactional
    public Review createReview(Review reviewDto, MultipartFile imageFile) throws Exception {
        // 구매 내역 검증
        if (reviewDto.getPurchase() == null || reviewDto.getPurchase().getPurchaseId() == null) {
            throw new RuntimeException("구매 내역이 누락되었습니다.");
        }
        Purchase purchase = purchaseRepository.findById(reviewDto.getPurchase().getPurchaseId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 구매 내역입니다."));

        // 작성자 확인 및 중복 리뷰 체크
        Long writerId = reviewDto.getUser().getUserId();
        Optional<Review> existingReview = reviewRepository.findByPurchasePurchaseIdAndUserUserId(
                purchase.getPurchaseId(), writerId);
        if(existingReview.isPresent()){
            throw new RuntimeException("이미 리뷰를 작성하셨습니다.");
        }

        // 구매자/판매자 판별 및 리뷰 타입 설정
        Long buyerId  = purchase.getUser().getUserId();
        Long sellerId = purchase.getProduct().getUser().getUserId();

        String reviewType;
        if (writerId.equals(buyerId)) {
            reviewType = "BUYER_TO_SELLER";
        } else if (writerId.equals(sellerId)) {
            reviewType = "SELLER_TO_BUYER";
        } else {
            throw new RuntimeException("해당 구매 내역과 관계없는 사용자입니다.");
        }

        // Review 엔티티 생성 및 저장
        Review review = new Review();
        review.setTitle(reviewDto.getTitle());
        review.setContent(reviewDto.getContent());
        review.setUser(reviewDto.getUser());
        review.setPurchase(purchase);
        review.setProduct(purchase.getProduct());
        review.setReviewType(reviewType);

        Review savedReview = reviewRepository.save(review);

        // 리뷰 이미지 저장 (새로운 ReviewImage 엔티티 사용)
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = storeFile(imageFile);
            ReviewImage reviewImage = new ReviewImage();
            reviewImage.setImageName(fileName);
            reviewImage.setReview(savedReview);
            reviewImageRepository.save(reviewImage);
            // 필요하면 savedReview.getReviewImages().add(reviewImage); (양방향 관계 설정 시)
        }

        return savedReview;
    }

    // 파일 저장 로직 (공통)
    private String storeFile(MultipartFile file) throws Exception {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename().replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }

//    // 리뷰 수정
//    public Review updateReview(Long reviewId, Review newReview) {
//        Review existingReview = reviewRepository.findById(reviewId)
//                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다."));
//        existingReview.setTitle(newReview.getTitle());
//        existingReview.setContent(newReview.getContent());
//        return reviewRepository.save(existingReview);
//    }
//
//    // 리뷰 삭제
//    public void deleteReview(Long reviewId) {
//        if (!reviewRepository.existsById(reviewId)) {
//            throw new RuntimeException("리뷰를 찾을 수 없습니다.");
//        }
//        reviewRepository.deleteById(reviewId);
//    }

    // 판매자 또는 구매자 기준 리뷰 조회 (DTO 변환 시 ReviewImage 사용)
    @Transactional
    public List<SellerReviewResponseDto> getReviewsForTarget(Long targetUserId) {
        return reviewRepository.findAll().stream()
                .filter(r -> {
                    String type = r.getReviewType();
                    if ("BUYER_TO_SELLER".equals(type)) {
                        return r.getProduct() != null &&
                                r.getProduct().getUser() != null &&
                                r.getProduct().getUser().getUserId().equals(targetUserId);
                    } else if ("SELLER_TO_BUYER".equals(type)) {
                        return r.getPurchase() != null &&
                                r.getPurchase().getUser() != null &&
                                r.getPurchase().getUser().getUserId().equals(targetUserId);
                    }
                    return false;
                })
                .map(r -> {
                    SellerReviewResponseDto dto = new SellerReviewResponseDto();
                    dto.setReviewerId(r.getUser().getUserId());
                    dto.setReviewerName(r.getUser().getUsername());
                    dto.setReviewTitle(r.getTitle());
                    dto.setReviewContent(r.getContent());
                    dto.setProductId(r.getProduct().getProductId());
                    dto.setProductTitle(r.getProduct().getTitle());

                    // reviewImages 중 첫 번째 이미지 URL 생성 (리뷰 이미지가 없으면 null)
                    if (r.getReviewImages() != null && !r.getReviewImages().isEmpty()) {
                        String url = "http://localhost:8090/review/images/" + r.getReviewImages().get(0).getImageName();
                        dto.setImage(url);
                    } else {
                        dto.setImage(null);
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
