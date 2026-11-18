package com.pickit.backend.category.controller;

import com.pickit.backend.category.dto.response.CategoryResponse;
import com.pickit.backend.category.service.CategoryService;
import com.pickit.backend.common.ApiResponse;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/api/categories")
    public ApiResponse<List<CategoryResponse>> getCategories() {
        return ApiResponse.ok(categoryService.getCategories());
    }
}
