package com.pickit.backend.post.dto.request;

import java.util.List;

public record UpdatePostRequest(
        String wantItemName,
        String preferredLocation,
        String preferredTime,
        String description,
        List<String> imageUrls
) {
}
