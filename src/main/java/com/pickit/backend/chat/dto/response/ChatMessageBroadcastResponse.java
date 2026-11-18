package com.pickit.backend.chat.dto.response;

import com.pickit.backend.chat.entity.ChatMessage;
import com.pickit.backend.chat.entity.ChatMessageType;
import java.time.LocalDateTime;

/** 구독자(/sub/chat-rooms/{roomId})에게 내려주는 메시지 */
public record ChatMessageBroadcastResponse(
        Long messageId,
        Long roomId,
        Long senderId,
        ChatMessageType messageType,
        String content,
        String imageUrl,
        LocalDateTime createdAt
) {
    public static ChatMessageBroadcastResponse from(ChatMessage message) {
        return new ChatMessageBroadcastResponse(
                message.getMessageId(),
                message.getRoom().getRoomId(),
                message.getSenderId(),
                message.getMessageType(),
                message.getContent(),
                message.getImageUrl(),
                message.getCreatedAt()
        );
    }
}
