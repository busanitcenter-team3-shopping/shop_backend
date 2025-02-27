package com.secure.shopbackend.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.secure.shopbackend.dtos.Category;
import com.secure.shopbackend.dtos.Product;
import com.secure.shopbackend.dtos.User;
import com.secure.shopbackend.repositories.ProductRepository;
import com.secure.shopbackend.repositories.UserRepository;
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
import java.util.List;

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

    //@Transactional
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProduct(
            @AuthenticationPrincipal UserDetails userDetails,
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
//    //상품 조회
//    @GetMapping
//    public List<Product> getAllProducts() {
//        return productRepository.findAll();
//    }

    @GetMapping
    public ResponseEntity<List<Product>> getProducts(
            @RequestParam(required = false, defaultValue = "ALL") Category category,
            @RequestParam(required = false, defaultValue = "") String search) {

        List<Product> products = productService.getProducts(category, search);
        return ResponseEntity.ok(products);
    }

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

}
