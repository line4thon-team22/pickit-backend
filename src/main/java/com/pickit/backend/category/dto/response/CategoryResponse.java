package com.pickit.backend.category.dto.response;

import com.pickit.backend.category.entity.Category;

public record CategoryResponse(
        Long categoryId,
        String name
) {
    public static CategoryResponse from(Category category) {
        return new CategoryResponse(category.getCategoryId(), category.getName());
    }
}
