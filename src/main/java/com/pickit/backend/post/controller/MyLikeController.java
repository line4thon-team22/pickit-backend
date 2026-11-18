package com.pickit.backend.post.controller;

import com.pickit.backend.common.dto.ApiResponse;
import com.pickit.backend.post.dto.response.PostListResponse;
import com.pickit.backend.post.service.PostLikeService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/me/likes/posts")
public class MyLikeController {

    private final PostLikeService postLikeService;

    public MyLikeController(PostLikeService postLikeService) {
        this.postLikeService = postLikeService;
    }

    @GetMapping
    public ApiResponse<PostListResponse> getMyLikedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();
        return ApiResponse.success(postLikeService.getMyLikedPosts(userId, page, size));
    }
}
