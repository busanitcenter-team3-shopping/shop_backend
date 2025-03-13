package com.secure.shopbackend.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;


@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String absolutePath = Paths.get(uploadDir)
                .toAbsolutePath()
                .toString()
                .replace("\\", "/") + "/";

        // 기존 상품 이미지
        registry.addResourceHandler("/product/images/**")
                .addResourceLocations("file:" + absolutePath);

        // 새로 추가: 리뷰 이미지
        registry.addResourceHandler("/review/images/**")
                .addResourceLocations("file:" + absolutePath);

        System.out.println("경로 :" + absolutePath);
    }
}



