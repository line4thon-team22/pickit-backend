package com.pickit.backend.user.dto.response;

import com.pickit.backend.auth.entity.User;

public record UserResponse(
        Long userId,
        String username,
        String profileImageUrl,
        Double mannerScore,
        Boolean isPremium
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getUserId(),
                user.getUsername(),
                user.getProfileImageUrl(),
                user.getMannerScore(),
                user.getIsPremium()
        );
    }
}
