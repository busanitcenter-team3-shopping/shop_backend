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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private UserRepository userRepository;

    // 파일 저장 로직 (예시)
    @Value("${file.upload-dir}")
    private String uploadDir;


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

        List<Image> imageTest = imageRepository.findByProduct_ProductId(productId);
        System.out.println(imageTest);


        if (imageFiles != null && !imageFiles.isEmpty()) {
            List<Image> existingImages = new ArrayList<>(product.getImages());
            System.out.println("이미지: " + existingImages);

            // 기존 이미지
            if (!existingImages.isEmpty()) {
                // 실제 파일 삭제
                for (Image image : existingImages) {
                    Path filePath = Paths.get(uploadDir, image.getImageName());
                    try {
                        Files.deleteIfExists(filePath);
                        System.out.println("파일 삭제 완료: " + filePath);
                    } catch (Exception e) {
                        System.err.println("파일 삭제 중 오류 발생: " + filePath + " | " + e.getMessage());
                    }
                }
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

    public List<Product> getProductsByUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userRepository.findByEmail(userDetails.getEmail()).orElseThrow(() -> new RuntimeException("User Not Found"));
        return productRepository.findByUser_UserId(user.getUserId());
    }

    // 상품 삭제
    @Transactional
    public void deleteProduct(Long productId, UserDetailsImpl userDetails, List<MultipartFile> imageFiles) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product Not Found"));

        List<Image> imageTest = imageRepository.findByProduct_ProductId(productId);
        System.out.println("이미지테스트 :" + imageTest);

        if (!product.getUser().getEmail().equals(userDetails.getEmail())) {
            throw new RuntimeException("사ㅣㄱ제할 상품이 없습니다.");
        }

        if (imageTest != null && !imageTest.isEmpty()) {
            List<Image> existingImages = new ArrayList<>(product.getImages());
            System.out.println("이미지: " + existingImages);

            if (!imageTest.isEmpty()) {
                // 실제 파일 삭제
                for (Image image : imageTest) {
                    Path filePath = Paths.get(uploadDir, image.getImageName());
                    try {
                        Files.deleteIfExists(filePath);
                        System.out.println("파일 삭제 완료: " + filePath);
                    } catch (Exception e) {
                        System.err.println("파일 삭제 중 오류 발생: " + filePath + " | " + e.getMessage());
                    }
                }
                product.getImages().clear();    // 상품의 이미지 정리
                imageRepository.deleteAll(imageTest); // 상품에 있는걸 다 삭제
                productRepository.save(product);
            }
        }
        
        productRepository.delete(product);
        System.out.println("상품 삭제 요청: " + productId);

        Optional<Product> deletedProduct = productRepository.findById(productId);
        if (deletedProduct.isPresent()) {
            System.err.println("🚨 상품 삭제 실패! productId: " + productId);
        } else {
            System.out.println("✅ 상품 삭제 성공! productId: " + productId);
        }
    }
}
