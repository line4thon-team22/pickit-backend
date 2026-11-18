package com.pickit.backend.chat.dto.request;

import com.pickit.backend.chat.entity.ChatMessageType;

/** TEXT는 content, IMAGE는 imageUrl(채팅 이미지 업로드 API가 준 URL)이 필요하다. */
public record SendChatMessageRequest(
        ChatMessageType messageType,
        String content,
        String imageUrl
) {
}
