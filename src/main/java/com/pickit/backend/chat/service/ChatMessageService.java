package com.pickit.backend.chat.service;

import com.pickit.backend.chat.dto.request.SendChatMessageRequest;
import com.pickit.backend.chat.dto.response.ChatMessageBroadcastResponse;
import com.pickit.backend.chat.dto.response.ChatMessageListResponse;
import com.pickit.backend.chat.dto.response.ChatMessageResponse;
import com.pickit.backend.chat.entity.ChatMessage;
import com.pickit.backend.chat.entity.ChatMessageType;
import com.pickit.backend.chat.entity.ChatRoom;
import com.pickit.backend.chat.repository.ChatMessageRepository;
import com.pickit.backend.chat.repository.ChatRoomRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class ChatMessageService {

    private static final int MAX_PAGE_SIZE = 100;
    private static final String CHAT_IMAGE_URL_PREFIX = "/images/chats/";

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;

    public ChatMessageService(ChatMessageRepository chatMessageRepository, ChatRoomRepository chatRoomRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatRoomRepository = chatRoomRepository;
    }

    /**
     * 커서(messageId)보다 이전 메시지를 최신 쪽부터 size개 조회한다. cursor가 없으면 가장 최근 메시지부터 조회한다.
     * 응답의 messages는 화면에 그리는 순서(오래된 메시지 -> 최신 메시지)로 내려가고,
     * nextCursor는 더 오래된 메시지가 남아 있을 때 이번 결과에서 가장 오래된 messageId, 없으면 null이다.
     */
    public ChatMessageListResponse getMessages(Long userId, Long roomId, Long cursor, int size) {
        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "size는 1 이상 " + MAX_PAGE_SIZE + " 이하여야 합니다.");
        }

        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "채팅방을 찾을 수 없습니다."));
        if (!room.isParticipant(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "채팅방에 접근할 권한이 없습니다.");
        }

        // 더 있는지 확인하려고 1개를 더 조회한다.
        PageRequest pageable = PageRequest.of(0, size + 1);
        List<ChatMessage> found = cursor == null
                ? chatMessageRepository.findByRoomOrderByMessageIdDesc(room, pageable)
                : chatMessageRepository.findByRoomAndMessageIdLessThanOrderByMessageIdDesc(room, cursor, pageable);

        boolean hasMore = found.size() > size;
        List<ChatMessage> page = new ArrayList<>(hasMore ? found.subList(0, size) : found);

        Long nextCursor = hasMore ? page.get(page.size() - 1).getMessageId() : null;

        Collections.reverse(page);
        List<ChatMessageResponse> messages = page.stream().map(ChatMessageResponse::from).toList();
        return new ChatMessageListResponse(messages, nextCursor);
    }

    /** WebSocket으로 받은 메시지를 검증하고 저장한다. 구독자 전송은 호출한 쪽에서 한다. */
    @Transactional
    public ChatMessageBroadcastResponse saveMessage(Long userId, Long roomId, SendChatMessageRequest request) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "채팅방을 찾을 수 없습니다."));
        if (!room.isParticipant(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "채팅방에 접근할 권한이 없습니다.");
        }
        if (request == null || request.messageType() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "messageType은 필수입니다.");
        }

        String content = null;
        String imageUrl = null;
        if (request.messageType() == ChatMessageType.TEXT) {
            if (request.content() == null || request.content().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "메시지 내용이 비어 있습니다.");
            }
            content = request.content();
        } else if (request.messageType() == ChatMessageType.IMAGE) {
            if (request.imageUrl() == null || !request.imageUrl().startsWith(CHAT_IMAGE_URL_PREFIX)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "올바르지 않은 이미지 URL입니다.");
            }
            imageUrl = request.imageUrl();
        } else {
            // APPOINTMENT 메시지는 교환 확정 시 서버가 직접 만든다.
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "전송할 수 없는 메시지 타입입니다.");
        }

        ChatMessage saved = chatMessageRepository.saveAndFlush(
                new ChatMessage(room, userId, request.messageType(), content, imageUrl));
        return ChatMessageBroadcastResponse.from(saved);
    }
}
