package com.pickit.backend.chat.websocket;

import com.pickit.backend.auth.util.JwtProvider;
import com.pickit.backend.chat.entity.ChatRoom;
import com.pickit.backend.chat.repository.ChatRoomRepository;
import java.security.Principal;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

/**
 * STOMP CONNECT 프레임의 Authorization 헤더(Bearer 액세스 토큰)를 검증한다.
 * 검증에 성공하면 세션의 사용자(Principal)를 로그인 유저 ID로 설정하고, 실패하면 연결을 거부한다.
 * 구독(SUBSCRIBE)은 채팅방 참여자만 /sub/chat-rooms/{roomId}를 구독할 수 있도록 확인한다.
 */
@Component
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private static final Pattern CHAT_ROOM_DESTINATION = Pattern.compile("^/sub/chat-rooms/(\\d{1,18})$");
    private static final String USER_QUEUE_PREFIX = "/user/queue/";

    private final JwtProvider jwtProvider;
    private final ChatRoomRepository chatRoomRepository;

    public StompAuthChannelInterceptor(JwtProvider jwtProvider, ChatRoomRepository chatRoomRepository) {
        this.jwtProvider = jwtProvider;
        this.chatRoomRepository = chatRoomRepository;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new MessageDeliveryException("인증 토큰이 필요합니다.");
            }
            String token = authHeader.substring(7);
            if (!jwtProvider.validateToken(token)) {
                throw new MessageDeliveryException("유효하지 않은 토큰입니다.");
            }
            Long userId = jwtProvider.getUserIdFromToken(token);
            accessor.setUser(new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList()));
        } else if (accessor != null && StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            validateSubscription(accessor);
        }
        return message;
    }

    private void validateSubscription(StompHeaderAccessor accessor) {
        Principal user = accessor.getUser();
        String destination = accessor.getDestination();
        if (user == null || destination == null) {
            throw new MessageDeliveryException("인증이 필요합니다.");
        }
        // 오류 전달용 개인 큐는 본인만 받으므로 허용한다.
        if (destination.startsWith(USER_QUEUE_PREFIX)) {
            return;
        }
        Matcher matcher = CHAT_ROOM_DESTINATION.matcher(destination);
        if (!matcher.matches()) {
            throw new MessageDeliveryException("구독할 수 없는 경로입니다.");
        }
        Long roomId = Long.parseLong(matcher.group(1));
        Long userId = Long.parseLong(user.getName());
        ChatRoom room = chatRoomRepository.findById(roomId).orElse(null);
        if (room == null || !room.isParticipant(userId)) {
            throw new MessageDeliveryException("채팅방에 접근할 권한이 없습니다.");
        }
    }
}
