package com.pickit.backend.chat.dto.response;

public record CreateChatRoomResponse(
        Long roomId,
        boolean isNew
) {
}
