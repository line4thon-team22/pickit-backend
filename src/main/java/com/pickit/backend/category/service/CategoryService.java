package com.pickit.backend.category.service;

import com.pickit.backend.category.dto.response.CategoryResponse;
import com.pickit.backend.category.repository.CategoryRepository;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CategoryService {

    // 팝업 필터 선택지와 같은 순서로 반환한다.
    private static final Sort CATEGORY_ORDER = Sort.by("name", "categoryId");

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /** 팝업 카테고리와 관심사 카테고리는 같은 categories 테이블과 ID를 사용한다. */
    public List<CategoryResponse> getCategories() {
        return categoryRepository.findAll(CATEGORY_ORDER).stream()
                .map(CategoryResponse::from)
                .toList();
    }
}
