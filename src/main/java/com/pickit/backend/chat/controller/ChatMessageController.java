package com.pickit.backend.chat.controller;

import com.pickit.backend.chat.dto.response.ChatMessageListResponse;
import com.pickit.backend.chat.service.ChatMessageService;
import com.pickit.backend.common.dto.ApiResponse;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat-rooms/{roomId}/messages")
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    public ChatMessageController(ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    @GetMapping
    public ApiResponse<ChatMessageListResponse> getMessages(
            @PathVariable Long roomId,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "30") int size,
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();
        return ApiResponse.success(chatMessageService.getMessages(userId, roomId, cursor, size));
    }
}
