package com.pickit.backend.post.dto.response;

import com.pickit.backend.post.entity.ExchangePost;
import com.pickit.backend.post.entity.PostImage;
import com.pickit.backend.post.service.PostCountResolver;
import com.pickit.backend.post.service.PostStateResolver;
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
            String popupName,
            String tab,
            String tag,
            long likeCount,
            long chatCount
    ) {
        public static PostSummary from(ExchangePost post, boolean isLiked, PostStateResolver.PostState state,
                                       PostCountResolver.PostCounts counts) {
            if (counts == null) {
                counts = PostCountResolver.EMPTY;
            }
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
                    post.getPopup().getName(),
                    state.tab(),
                    state.tag(),
                    counts.likeCount(),
                    counts.chatCount()
            );
        }
    }
}
