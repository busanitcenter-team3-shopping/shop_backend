package com.secure.shopbackend.controller;

import com.secure.shopbackend.dtos.Category;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class EnumController {

  // 카테고리
  @GetMapping("/enum")
  public Category[] getCategories() {
    return Category.values();
  }
}
