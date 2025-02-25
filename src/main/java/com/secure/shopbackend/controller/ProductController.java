package com.secure.shopbackend.controller;


import com.secure.shopbackend.dtos.Product;
import com.secure.shopbackend.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin("*")
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    //등록
    @PostMapping("/create")
    public ResponseEntity<?> createProduct(
            @RequestPart("product") @Valid Product productDto,
            BindingResult bindingResult,
            @RequestPart("image") MultipartFile imageFile) {

        if (bindingResult.hasErrors()) {
            String error = bindingResult.getFieldError().getDefaultMessage();
            return ResponseEntity.badRequest().body(error);
        }

        try {
            Product product = productService.createProduct(productDto, imageFile);
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }




}
