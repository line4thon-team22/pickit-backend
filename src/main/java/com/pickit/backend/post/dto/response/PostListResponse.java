package com.pickit.backend.post.dto.response;

import com.pickit.backend.post.entity.ExchangePost;
import com.pickit.backend.post.entity.PostImage;
import java.time.LocalDateTime;
import java.util.List;

public record PostListResponse(
        List<PostSummary> content,
        int page,
        int size
) {
    public record PostSummary(
            Long postId,
            Long userId,
            Long popupId,
            Long categoryId,
            String haveItemName,
            String wantItemName,
            String preferredLocation,
            String preferredTime,
            String description,
            String status,
            List<String> images,
            LocalDateTime createdAt,
            Boolean isLiked,
            String popupName
    ) {
        public static PostSummary from(ExchangePost post, boolean isLiked) {
            List<String> imageUrls = post.getImages().stream()
                    .sorted(java.util.Comparator.comparing(PostImage::getSortOrder))
                    .map(PostImage::getImageUrl)
                    .toList();

            return new PostSummary(
                    post.getPostId(),
                    post.getUserId(),
                    post.getPopup().getPopupId(),
                    post.getCategory().getCategoryId(),
                    post.getHaveItemName(),
                    post.getWantItemName(),
                    post.getPreferredLocation(),
                    post.getPreferredTime(),
                    post.getDescription(),
                    post.getStatus(),
                    imageUrls,
                    post.getCreatedAt(),
                    isLiked,
                    post.getPopup().getName()
            );
        }
    }
}
