package com.pickit.backend.chat.dto.request;

import jakarta.validation.constraints.NotNull;

public record CreateChatRoomRequest(
        @NotNull(message = "postId는 필수입니다.")
        Long postId
) {
}
