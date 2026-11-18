package com.pickit.backend.auth.dto.response;

public record GoogleLoginResponse(
        String accessToken,
        String refreshToken,
        Boolean isNewUser
) {
}
