package com.pickit.backend.post.controller;

import com.pickit.backend.common.dto.ApiResponse;
import com.pickit.backend.post.dto.request.CreatePostRequest;
import com.pickit.backend.post.dto.request.UpdatePostRequest;
import com.pickit.backend.post.dto.response.PostDetailResponse;
import com.pickit.backend.post.dto.response.PostListResponse;
import com.pickit.backend.post.service.PostService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public ApiResponse<PostListResponse> getPosts(
            @RequestParam(required = false) Long popupId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "LATEST") String sort,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String tab,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication
    ) {
        Long currentUserId = authentication != null && authentication.getPrincipal() instanceof Long ? (Long) authentication.getPrincipal() : null;
        PostListResponse response = postService.getPosts(popupId, categoryId, status, sort, keyword, tab, page, size, currentUserId);
        return ApiResponse.success(response);
    }

    @GetMapping("/recommended")
    public ApiResponse<List<PostListResponse.PostSummary>> getRecommendedPosts(
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();
        List<PostListResponse.PostSummary> response = postService.getRecommendedPosts(userId, size);
        return ApiResponse.success(response);
    }

    @GetMapping("/{postId}")
    public ApiResponse<PostDetailResponse> getPostDetail(
            @PathVariable Long postId,
            Authentication authentication
    ) {
        Long currentUserId = authentication != null && authentication.getPrincipal() instanceof Long ? (Long) authentication.getPrincipal() : null;
        PostDetailResponse response = postService.getPostDetail(postId, currentUserId);
        return ApiResponse.success(response);
    }

    @PostMapping
    public ApiResponse<Map<String, Long>> createPost(
            @Valid @RequestBody CreatePostRequest request,
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();
        Long postId = postService.createPost(userId, request);
        return ApiResponse.success(Map.of("postId", postId));
    }

    @PatchMapping("/{postId}")
    public ApiResponse<Map<String, Long>> updatePost(
            @PathVariable Long postId,
            @Valid @RequestBody UpdatePostRequest request,
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();
        Long updatedPostId = postService.updatePost(userId, postId, request);
        return ApiResponse.success(Map.of("postId", updatedPostId));
    }

    @DeleteMapping("/{postId}")
    public ApiResponse<Void> deletePost(
            @PathVariable Long postId,
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();
        postService.deletePost(userId, postId);
        return ApiResponse.success(null);
    }

    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Map<String, List<String>>> uploadPostImages(
            @RequestParam("files") List<MultipartFile> files
    ) {
        List<String> imageUrls = postService.uploadImages(files);
        return ApiResponse.success(Map.of("imageUrls", imageUrls));
    }
}
