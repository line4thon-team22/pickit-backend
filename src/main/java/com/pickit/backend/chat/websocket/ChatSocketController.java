package com.pickit.backend.chat.websocket;

import com.pickit.backend.chat.dto.request.SendChatMessageRequest;
import com.pickit.backend.chat.dto.response.ChatMessageBroadcastResponse;
import com.pickit.backend.chat.service.ChatMessageService;
import com.pickit.backend.common.dto.ApiResponse;
import java.security.Principal;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class ChatSocketController {

    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatSocketController(ChatMessageService chatMessageService, SimpMessagingTemplate messagingTemplate) {
        this.chatMessageService = chatMessageService;
        this.messagingTemplate = messagingTemplate;
    }

    /** 클라이언트가 /pub/chat-rooms/{roomId}로 보내면 저장 후 /sub/chat-rooms/{roomId} 구독자 모두에게 전달한다. */
    @MessageMapping("/chat-rooms/{roomId}")
    public void sendMessage(
            @DestinationVariable Long roomId,
            @Payload SendChatMessageRequest request,
            Principal principal
    ) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증이 필요합니다.");
        }
        Long userId = Long.parseLong(principal.getName());
        ChatMessageBroadcastResponse message = chatMessageService.saveMessage(userId, roomId, request);
        messagingTemplate.convertAndSend("/sub/chat-rooms/" + roomId, message);
    }

    /** 오류는 보낸 사람에게만 /user/queue/errors 로 전달한다. */
    @MessageExceptionHandler(ResponseStatusException.class)
    @SendToUser("/queue/errors")
    public ApiResponse<Void> handleStatusException(ResponseStatusException e) {
        String message = e.getReason() != null ? e.getReason() : "요청을 처리할 수 없습니다.";
        return new ApiResponse<>(e.getStatusCode().value(), message, null);
    }

    @MessageExceptionHandler(Exception.class)
    @SendToUser("/queue/errors")
    public ApiResponse<Void> handleException(Exception e) {
        return new ApiResponse<>(400, "메시지 형식이 올바르지 않습니다.", null);
    }
}
