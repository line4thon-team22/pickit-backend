package com.pickit.backend.user.controller;

import com.pickit.backend.common.dto.ApiResponse;
import com.pickit.backend.user.dto.request.UpdateProfileRequest;
import com.pickit.backend.user.dto.response.ProfileImageResponse;
import com.pickit.backend.user.dto.response.UserProfileResponse;
import com.pickit.backend.user.dto.response.UserResponse;
import com.pickit.backend.user.service.UserService;
import jakarta.validation.Valid;
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
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ApiResponse<UserResponse> getMyProfile(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        UserResponse response = userService.getMyProfile(userId);
        return ApiResponse.success(response);
    }

    @GetMapping("/{userId}")
    public ApiResponse<UserProfileResponse> getUserProfile(@PathVariable Long userId) {
        UserProfileResponse response = userService.getUserProfile(userId);
        return ApiResponse.success(response);
    }

    @PatchMapping("/me")
    public ApiResponse<UserResponse> updateMyProfile(Authentication authentication, @Valid @RequestBody UpdateProfileRequest request) {
        Long userId = (Long) authentication.getPrincipal();
        UserResponse response = userService.updateMyProfile(userId, request);
        return ApiResponse.success(response);
    }

    @PostMapping("/me/profile-image")
    public ApiResponse<ProfileImageResponse> uploadProfileImage(Authentication authentication, @RequestParam("file") MultipartFile file) {
        Long userId = (Long) authentication.getPrincipal();
        ProfileImageResponse response = userService.uploadProfileImage(userId, file);
        return ApiResponse.success(response);
    }

    @DeleteMapping("/me/profile-image")
    public ApiResponse<Void> deleteProfileImage(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        userService.deleteProfileImage(userId);
        return ApiResponse.success(null);
    }
}
