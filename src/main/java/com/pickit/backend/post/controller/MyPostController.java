package com.pickit.backend.post.controller;

import com.pickit.backend.common.dto.ApiResponse;
import com.pickit.backend.post.dto.response.PostListResponse;
import com.pickit.backend.post.service.PostService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/me/posts")
public class MyPostController {

    private final PostService postService;

    public MyPostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public ApiResponse<PostListResponse> getMyPosts(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();
        return ApiResponse.success(postService.getMyPosts(userId, status, page, size));
    }
}
