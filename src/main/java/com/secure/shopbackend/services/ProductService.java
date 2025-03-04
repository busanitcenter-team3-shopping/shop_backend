package com.secure.shopbackend.services;


import com.secure.shopbackend.dtos.Category;
import com.secure.shopbackend.dtos.Image;
import com.secure.shopbackend.dtos.Product;
import com.secure.shopbackend.dtos.User;
import com.secure.shopbackend.repositories.ImageRepository;
import com.secure.shopbackend.repositories.ProductRepository;
import com.secure.shopbackend.repositories.UserRepository;
import com.secure.shopbackend.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private UserRepository userRepository;


    // 유저의 토큰값을 찾아올수 없다하여 "ROLE_USER"을 줌으로써 무조건 토큰의 값을 읽어올수 있도록 변경
    // 이미지 테이블에 product_id가 없어서 이미지를 참조할 부분이 없어서 추가
    // product 테이블에 필요없는 category_id, image_id 제거
    // 카테고리를 enum으로 변경함으로써 ALTER TABLE product ADD COLUMN category VARCHAR(255); 추가
    // ALTER TABLE image ADD COLUMN product_id BIGINT; 이미지 테이블에 product_id를 참조할 내용 추가


    // 상품 등록: 상품 정보와 이미지 파일을 받아서 처리
//    @Transactional
    public Product createProduct(@AuthenticationPrincipal UserDetailsImpl userDetails, Product productDto, List<MultipartFile> imageFiles) throws Exception {

        String email = userDetails.getEmail();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User Not Found"));

        Product product = new Product();
        product.setTitle(productDto.getTitle());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setCategory(productDto.getCategory());
        product.setStatus("판매중");
        product.setUser(user);

        product = productRepository.save(product);

        List<Image> imageList = new ArrayList<>();

        for (MultipartFile imageFile : imageFiles) {
            String fileName = storeFile(imageFile);

            Image image = new Image();
            image.setImageName(fileName);
            image.setProduct(product);
            imageList.add(image);
        }
        imageRepository.saveAll(imageList);
        product.setImages(imageList);

        return productRepository.save(product);
    }

    //상품 수정
    @Transactional
    public Product updateProduct(Long productId, UserDetailsImpl userDetails, Product updatedProduct, List<MultipartFile> imageFiles) throws Exception {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product Not Found"));

        product.setTitle(updatedProduct.getTitle());
        product.setDescription(updatedProduct.getDescription());
        product.setPrice(updatedProduct.getPrice());
        product.setCategory(updatedProduct.getCategory());
        product.setStatus("판매중");

        if (imageFiles != null && !imageFiles.isEmpty()) {
            List<Image> existingImages = new ArrayList<>(product.getImages());

            // 기존 이미지
            if (!existingImages.isEmpty()) {
                product.getImages().clear();    // 상품의 이미지 정리
                productRepository.save(product);    // 상품 저장
                imageRepository.deleteAll(existingImages);  // 이미지 지우기
                System.out.println("기존 이미지 삭제 완료");
            }
            // 새상품
            List<Image> newImages = new ArrayList<>();
            for (MultipartFile imageFile : imageFiles) {
                String fileName = storeFile(imageFile);

                Image image = new Image();
                image.setImageName(fileName);
                image.setProduct(product);
                newImages.add(image);
            }

            product.setImages(newImages);
        }

        return productRepository.save(product);
    }

// 상품 상세
    public Product detailProduct (Long id) {

     return productRepository.findById(id).orElseThrow(() -> new RuntimeException("User Not Found"));

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

    public List<Product> getProducts(Category category, String search) {
        if (category == null || category.equals(Category.ALL)) {
            return productRepository.findByTitleContainingIgnoreCase(search);
        } else {
            return productRepository.findByCategoryAndTitleContainingIgnoreCase(category, search);
        }

    }

    // 상품 삭제
    @Transactional
    public void deleteProduct(Long productId, List<MultipartFile> imageFiles) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product Not Found"));

        if (product.getImages() != null && !product.getImages().isEmpty()) {
            List<Image> existingImages = new ArrayList<>(product.getImages());

        if(imageFiles != null && !imageFiles.isEmpty()) {
            for(Image image : existingImages) {
                Path filePath = Paths.get(uploadDir, image.getImageName());
                try {
                    Files.deleteIfExists(filePath);
                }catch (Exception e) {
                e.printStackTrace();}
            }
            }
        product.getImages().clear();
        productRepository.save(product);
        imageRepository.deleteAll(existingImages);
        }
        imageRepository.deleteByProduct(product);
        System.out.println("id 확인 : "+ productId);
        productRepository.deleteProductById(productId);
    }
}
