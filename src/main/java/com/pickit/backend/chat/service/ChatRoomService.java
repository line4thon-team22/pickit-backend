package com.pickit.backend.chat.service;

import com.pickit.backend.appointment.entity.TradeAppointment;
import com.pickit.backend.appointment.repository.TradeAppointmentRepository;
import com.pickit.backend.appointment.repository.TradeReviewRepository;
import com.pickit.backend.auth.entity.User;
import com.pickit.backend.auth.repository.UserRepository;
import com.pickit.backend.chat.dto.response.ChatRoomDetailResponse;
import com.pickit.backend.chat.dto.response.ChatRoomListItemResponse;
import com.pickit.backend.chat.dto.response.CreateChatRoomResponse;
import com.pickit.backend.chat.entity.ChatRoom;
import com.pickit.backend.chat.repository.ChatMessageRepository;
import com.pickit.backend.chat.repository.ChatRoomRepository;
import com.pickit.backend.common.service.FileService;
import com.pickit.backend.post.entity.ExchangePost;
import com.pickit.backend.post.repository.ExchangePostRepository;
import com.pickit.backend.post.service.PostStateResolver;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ExchangePostRepository exchangePostRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final FileService fileService;
    private final TradeAppointmentRepository appointmentRepository;
    private final PostStateResolver postStateResolver;
    private final TradeReviewRepository reviewRepository;

    public ChatRoomService(ChatRoomRepository chatRoomRepository, ExchangePostRepository exchangePostRepository,
                           ChatMessageRepository chatMessageRepository, UserRepository userRepository,
                           FileService fileService, TradeAppointmentRepository appointmentRepository,
                           PostStateResolver postStateResolver, TradeReviewRepository reviewRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.exchangePostRepository = exchangePostRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
        this.fileService = fileService;
        this.appointmentRepository = appointmentRepository;
        this.postStateResolver = postStateResolver;
        this.reviewRepository = reviewRepository;
    }

    /** 교환글에서 채팅을 시작한다. 같은 글에 이미 만든 방이 있으면 그 방을 반환한다. */
    @Transactional
    public CreateChatRoomResponse createRoom(Long buyerId, Long postId) {
        ExchangePost post = exchangePostRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "교환글을 찾을 수 없습니다."));

        if (post.getUserId().equals(buyerId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "본인의 교환글에는 채팅방을 만들 수 없습니다.");
        }

        var existingRoom = chatRoomRepository.findByPostAndBuyerId(post, buyerId);
        // 이미 대화 중인 방은 그대로 열 수 있고, 새 채팅만 현장 글 인증 조건을 확인한다.
        if (existingRoom.isEmpty()
                && PostStateResolver.TAB_ONSITE.equals(postStateResolver.resolve(post).tab())
                && !postStateResolver.isVerifiedNow(buyerId, post.getPopup().getPopupId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "현장 교환글은 해당 팝업에서 위치 인증을 한 분만 채팅을 시작할 수 있습니다.");
        }

        // 이미 예약 중이거나 교환 완료된 글에는 새 채팅을 시작할 수 없다. (기존 방은 위에서 그대로 열린다.)
        if (existingRoom.isEmpty() && !"OPEN".equals(post.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 교환이 진행 중이거나 완료된 글입니다.");
        }

        return existingRoom
                .map(room -> new CreateChatRoomResponse(room.getRoomId(), false))
                .orElseGet(() -> {
                    try {
                        ChatRoom room = chatRoomRepository.saveAndFlush(new ChatRoom(post, post.getUserId(), buyerId));
                        return new CreateChatRoomResponse(room.getRoomId(), true);
                    } catch (DataIntegrityViolationException e) {
                        // 동시 요청으로 UK(post_id, buyer_id)에 걸린 경우 먼저 만들어진 방을 반환한다.
                        ChatRoom existing = chatRoomRepository.findByPostAndBuyerId(post, buyerId)
                                .orElseThrow(() -> e);
                        return new CreateChatRoomResponse(existing.getRoomId(), false);
                    }
                });
    }

    public List<ChatRoomListItemResponse> getMyRooms(Long userId, int page, int size) {
        return chatRoomRepository.findMyRooms(userId, PageRequest.of(page, size)).getContent().stream()
                .map(room -> {
                    Long opponentId = room.getOpponentId(userId);
                    User opponent = userRepository.findById(opponentId).orElse(null);
                    return ChatRoomListItemResponse.of(room, opponentId, opponent,
                            chatMessageRepository.findFirstByRoomOrderByMessageIdDesc(room).orElse(null));
                })
                .toList();
    }

    public ChatRoomDetailResponse getRoomDetail(Long userId, Long roomId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "채팅방을 찾을 수 없습니다."));

        if (!room.isParticipant(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "채팅방에 접근할 권한이 없습니다.");
        }

        Long opponentId = room.getOpponentId(userId);
        User opponent = userRepository.findById(opponentId).orElse(null);
        TradeAppointment appointment = appointmentRepository.findByRoom(room).orElse(null);
        boolean reviewed = appointment != null && reviewRepository.existsByAppointmentAndReviewerId(appointment, userId);
        return ChatRoomDetailResponse.of(room, opponentId, opponent, appointment, reviewed);
    }

    /** 채팅 사진을 저장하고 URL을 반환한다. 메시지 전송은 이후 WebSocket(IMAGE)으로 한다. */
    public String uploadImage(Long userId, Long roomId, MultipartFile file) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "채팅방을 찾을 수 없습니다."));

        if (!room.isParticipant(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "채팅방에 접근할 권한이 없습니다.");
        }
        String contentType = file == null ? null : file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미지 파일만 업로드할 수 있습니다.");
        }

        return fileService.storeFile(file, "chats");
    }
}
