package com.pickit.backend.chat.dto.response;

import java.util.List;

public record ChatMessageListResponse(
        List<ChatMessageResponse> messages,
        Long nextCursor
) {
}
