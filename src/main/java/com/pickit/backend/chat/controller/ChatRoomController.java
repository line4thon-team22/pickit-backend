package com.pickit.backend.chat.controller;

import com.pickit.backend.chat.dto.request.CreateChatRoomRequest;
import com.pickit.backend.chat.dto.response.ChatRoomDetailResponse;
import com.pickit.backend.chat.dto.response.ChatRoomListItemResponse;
import com.pickit.backend.chat.dto.response.CreateChatRoomResponse;
import com.pickit.backend.chat.service.ChatRoomService;
import com.pickit.backend.common.dto.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/chat-rooms")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    public ChatRoomController(ChatRoomService chatRoomService) {
        this.chatRoomService = chatRoomService;
    }

    @PostMapping
    public ApiResponse<CreateChatRoomResponse> createRoom(
            @Valid @RequestBody CreateChatRoomRequest request,
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();
        return ApiResponse.success(chatRoomService.createRoom(userId, request.postId()));
    }

    @GetMapping
    public ApiResponse<List<ChatRoomListItemResponse>> getMyRooms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();
        return ApiResponse.success(chatRoomService.getMyRooms(userId, page, size));
    }

    @GetMapping("/{roomId}")
    public ApiResponse<ChatRoomDetailResponse> getRoomDetail(
            @PathVariable Long roomId,
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();
        return ApiResponse.success(chatRoomService.getRoomDetail(userId, roomId));
    }

    @PostMapping(value = "/{roomId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Map<String, String>> uploadImage(
            @PathVariable Long roomId,
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();
        return ApiResponse.success(Map.of("imageUrl", chatRoomService.uploadImage(userId, roomId, file)));
    }
}
