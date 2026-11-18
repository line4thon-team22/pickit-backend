package com.pickit.backend.post.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CreatePostRequest(
        @NotNull Long popupId,
        @NotNull Long categoryId,
        @NotBlank String haveItemName,
        @NotBlank String wantItemName,
        String preferredLocation,
        String preferredTime,
        String description,
        List<String> imageUrls
) {
}
