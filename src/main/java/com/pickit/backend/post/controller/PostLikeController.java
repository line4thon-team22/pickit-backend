package com.pickit.backend.post.controller;

import com.pickit.backend.common.dto.ApiResponse;
import com.pickit.backend.post.service.PostLikeService;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts/{postId}/likes")
public class PostLikeController {

    private final PostLikeService postLikeService;

    public PostLikeController(PostLikeService postLikeService) {
        this.postLikeService = postLikeService;
    }

    @PostMapping
    public ApiResponse<Map<String, Long>> likePost(
            @PathVariable Long postId,
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();
        Long likeId = postLikeService.likePost(userId, postId);
        return ApiResponse.success(Map.of("likeId", likeId));
    }

    @DeleteMapping
    public ApiResponse<Void> unlikePost(
            @PathVariable Long postId,
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();
        postLikeService.unlikePost(userId, postId);
        return ApiResponse.success(null);
    }
}
