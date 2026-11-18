package com.pickit.backend.auth.dto.response;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        UserSummary user
) {
    public record UserSummary(
            Long userId,
            String username,
            String profileImageUrl,
            Double mannerScore,
            Boolean isPremium
    ) {
    }
}
