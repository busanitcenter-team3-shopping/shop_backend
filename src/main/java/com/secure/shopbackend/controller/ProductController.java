package com.secure.shopbackend.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.secure.shopbackend.dtos.Category;
import com.secure.shopbackend.dtos.Product;
import com.secure.shopbackend.dtos.User;
import com.secure.shopbackend.repositories.ProductRepository;
import com.secure.shopbackend.repositories.UserRepository;
import com.secure.shopbackend.security.services.UserDetailsImpl;
import com.secure.shopbackend.services.ProductService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
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

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;
  @Autowired
  private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;

    // 상품 등록
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProduct(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestPart("files") List<MultipartFile> files,
            @RequestPart("product") String productJson) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        ObjectMapper objectMapper = new ObjectMapper();
        Product product;

        try {
            // JSON 문자열을 Product 객체로 변환
            System.out.println("Received productJson: " + productJson);

            product = objectMapper.readValue(productJson, Product.class);
            System.out.println(product);

            productService.createProduct(userDetails, product, files);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid product JSON format: " + e.getMessage());
        }

        return ResponseEntity.ok().build();
    }

    //전체 상품 조회
    @GetMapping("/main")
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }


  // 카테고리별 상품 받기
    @GetMapping
    public ResponseEntity<List<Product>> getProducts(
            @RequestParam(required = false, defaultValue = "ALL") Category category,
            @RequestParam(required = false, defaultValue = "") String search) {

        List<Product> products = productService.getProducts(category, search);
        return ResponseEntity.ok(products);
    }

//    //유저의 상품 조회
//    @GetMapping("/user-page")
//    public ResponseEntity<List<Product>> getUserProducts(@AuthenticationPrincipal UserDetailsImpl userDetails) {
//        if (userDetails == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//        List<Product> products = productService.getProductsByUser(userDetails);
//        return ResponseEntity.ok(products);
//    }

    //판매자 상품 조회
    @GetMapping("/user-page/{id}")
    public ResponseEntity<List<Product>> getSellerProducts(@PathVariable Long id) {
        List<Product> products = productRepository.findByUser_UserId(id);
        return ResponseEntity.ok(products);
    }



    // 해당 주소로 이미지 등록
    @Value("${file.upload-dir}")
    private String uploadDir;

    @GetMapping("/images/{imageName}")
    public ResponseEntity<Resource> getImage(@PathVariable String imageName) {
        try {
            Path imagePath = Paths.get(uploadDir + imageName);
            Resource image = new UrlResource(imagePath.toUri());

            if (image.exists() || image.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_PNG)
                        .body(image);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    //상세 상품 보기
    @GetMapping("/{id}")
    public ResponseEntity<?> getdetailProduct(@PathVariable Long id) {
      Product product = productService.detailProduct(id);
      System.out.println(product);
      return ResponseEntity.ok(product);
    }

// 상품 수정
@PutMapping("/{id}")
public ResponseEntity<?> updateProduct(
        @PathVariable Long id,
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestPart("product") String productJson,
        @RequestPart(value = "files", required = false) List<MultipartFile> files) {

    if (userDetails == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    ObjectMapper objectMapper = new ObjectMapper();
    Product updatedProduct;

    try {
        updatedProduct = objectMapper.readValue(productJson, Product.class);
        Product product = productService.updateProduct(id, userDetails, updatedProduct, files);
        return ResponseEntity.ok(product);
    } catch (Exception e) {
        return ResponseEntity.badRequest().body("에러: " + e.getMessage());
    }
}

// 삭제
@DeleteMapping("/{id}")
public ResponseEntity<?> deleteProduct(
        @PathVariable Long id,
        @RequestPart(value = "files", required = false) List<MultipartFile> files)
{

    try {
        productService.deleteProduct(id,files);
        return ResponseEntity.ok("삭제 성공");
    } catch (RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}
}
