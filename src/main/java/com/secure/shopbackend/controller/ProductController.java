package com.secure.shopbackend.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.secure.shopbackend.dtos.Product;
import com.secure.shopbackend.dtos.User;
import com.secure.shopbackend.repositories.UserRepository;
import com.secure.shopbackend.services.ProductService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;
  @Autowired
  private UserRepository userRepository;

    //등록
//    @Transactional
//    @PostMapping("/create")
//    public ResponseEntity<?> createProduct(@AuthenticationPrincipal UserDetails userDetails, @RequestPart(value = "file") List<MultipartFile> files, @RequestPart Product product) {
//        if(userDetails == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//
//        try {
//            productService.createProduct(product, files);
//        }catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        } catch (Exception e) {
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body("서버 오류가 발생했습니다. 다시 시도해주세요.");
//    }
//
//        return ResponseEntity.ok().build();
//    }

    @Transactional
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProduct(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestPart("files") List<MultipartFile> files,
            @RequestPart("product") String productJson) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String username = userDetails.getUsername();
        ObjectMapper objectMapper = new ObjectMapper();
        Product product;

        try {
            // JSON 문자열을 Product 객체로 변환
            System.out.println("Received productJson: " + productJson);

            product = objectMapper.readValue(productJson, Product.class);
            System.out.println(product);

            User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User Not Found"));
            System.out.println(user);
            product.setUser(user);
            productService.createProduct(product, files);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid product JSON format: " + e.getMessage());
        }

        return ResponseEntity.ok().build();
    }
}
