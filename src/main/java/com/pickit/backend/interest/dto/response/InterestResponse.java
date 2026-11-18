package com.pickit.backend.interest.dto.response;

import com.pickit.backend.category.dto.response.CategoryResponse;
import java.util.List;

public record InterestResponse(
        List<CategoryResponse> interests,
        int maxCount
) {
}
