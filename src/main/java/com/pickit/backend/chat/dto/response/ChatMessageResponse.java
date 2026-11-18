package com.pickit.backend.chat.dto.response;

import com.pickit.backend.chat.entity.ChatMessage;
import com.pickit.backend.chat.entity.ChatMessageType;
import java.time.LocalDateTime;

public record ChatMessageResponse(
        Long messageId,
        Long senderId,
        ChatMessageType messageType,
        String content,
        String imageUrl,
        LocalDateTime createdAt
) {
    public static ChatMessageResponse from(ChatMessage message) {
        return new ChatMessageResponse(
                message.getMessageId(),
                message.getSenderId(),
                message.getMessageType(),
                message.getContent(),
                message.getImageUrl(),
                message.getCreatedAt()
        );
    }
}
