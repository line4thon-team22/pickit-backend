package com.pickit.backend.user.dto.response;

import com.pickit.backend.auth.entity.User;

public record UserProfileResponse(
        Long userId,
        String username,
        String profileImageUrl,
        Double mannerScore
) {
    public static UserProfileResponse from(User user) {
        return new UserProfileResponse(
                user.getUserId(),
                user.getUsername(),
                user.getProfileImageUrl(),
                user.getMannerScore()
        );
    }
}
