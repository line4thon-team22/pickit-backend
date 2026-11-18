package com.pickit.backend.auth.dto.response;

public record RefreshTokenResponse(
        String accessToken,
        String refreshToken
) {
}
