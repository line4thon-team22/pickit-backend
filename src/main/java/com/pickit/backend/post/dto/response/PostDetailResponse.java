package com.pickit.backend.post.dto.response;

import com.pickit.backend.auth.entity.User;
import com.pickit.backend.post.entity.ExchangePost;
import com.pickit.backend.post.entity.PostImage;
import com.pickit.backend.user.dto.response.UserResponse;
import java.time.LocalDateTime;
import java.util.List;

public record PostDetailResponse(
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
        UserResponse writer,
        Boolean isLiked
) {
    public static PostDetailResponse of(ExchangePost post, User writer, boolean isLiked) {
        List<String> imageUrls = post.getImages().stream()
                .sorted(java.util.Comparator.comparing(PostImage::getSortOrder))
                .map(PostImage::getImageUrl)
                .toList();

        UserResponse writerResponse = writer != null ? UserResponse.from(writer) : null;

        return new PostDetailResponse(
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
                writerResponse,
                isLiked
        );
    }
}
