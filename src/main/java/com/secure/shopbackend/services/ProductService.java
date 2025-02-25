package com.secure.shopbackend.services;


import com.secure.shopbackend.dtos.Image;
import com.secure.shopbackend.dtos.Product;
import com.secure.shopbackend.repositories.ImageRepository;
import com.secure.shopbackend.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ImageRepository imageRepository;

    // 상품 등록: 상품 정보와 이미지 파일을 받아서 처리
    public Product createProduct(Product productDto, MultipartFile imageFile) throws Exception {

        Product product = new Product();
        product.setTitle(productDto.getTitle());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setCategory(productDto.getCategory());

        product.setUser(productDto.getUser());


//        product.setImage(image);

        Image image = new Image();
        String fileName = storeFile(imageFile);
        image.setImageName(fileName);
        imageRepository.save(image);


        product.setImages(List.of(image));

        return productRepository.save(product);
    }

    // 파일 저장 로직 (예시)
    @Value("${file.upload-dir}")
    private String uploadDir;

    private String storeFile(MultipartFile file) throws Exception {
        // 예시: 지정된 경로에 파일 저장 후, 파일명 반환
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }
}
