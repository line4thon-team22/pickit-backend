package com.pickit.backend.appointment.service;

import com.pickit.backend.appointment.dto.response.AppointmentCompleteResponse;
import com.pickit.backend.appointment.dto.response.AppointmentConfirmResponse;
import com.pickit.backend.appointment.dto.response.AppointmentListResponse;
import com.pickit.backend.appointment.entity.TradeAppointment;
import com.pickit.backend.appointment.repository.TradeAppointmentRepository;
import com.pickit.backend.appointment.repository.TradeReviewRepository;
import com.pickit.backend.auth.entity.User;
import com.pickit.backend.auth.repository.UserRepository;
import com.pickit.backend.chat.dto.response.ChatMessageBroadcastResponse;
import com.pickit.backend.chat.entity.ChatMessage;
import com.pickit.backend.chat.entity.ChatMessageType;
import com.pickit.backend.chat.entity.ChatRoom;
import com.pickit.backend.chat.repository.ChatMessageRepository;
import com.pickit.backend.chat.repository.ChatRoomRepository;
import com.pickit.backend.post.entity.ExchangePost;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class AppointmentService {

    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");

    private final TradeAppointmentRepository appointmentRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final TradeReviewRepository reviewRepository;

    public AppointmentService(TradeAppointmentRepository appointmentRepository,
                              ChatRoomRepository chatRoomRepository,
                              ChatMessageRepository chatMessageRepository,
                              UserRepository userRepository,
                              TradeReviewRepository reviewRepository) {
        this.appointmentRepository = appointmentRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
    }

    /** 교환 확정 결과. message는 커밋 후 채팅방 구독자에게 전달할 APPOINTMENT 메시지다. */
    public record ConfirmResult(AppointmentConfirmResponse appointment, ChatMessageBroadcastResponse message) {
    }

    /** 채팅방 참여자가 교환 장소를 정해 약속을 확정한다. 한 채팅방에는 약속이 하나만 만들어진다. */
    @Transactional
    public ConfirmResult confirm(Long userId, Long roomId, String meetingPlace) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "채팅방을 찾을 수 없습니다."));
        if (!room.isParticipant(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "채팅방에 접근할 권한이 없습니다.");
        }
        if (appointmentRepository.existsByRoom(room)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 교환 약속이 확정된 채팅방입니다.");
        }

        // 다른 채팅방에서 이미 약속이 확정돼 글이 예약 중/완료면 또 확정할 수 없다.
        if (!"OPEN".equals(room.getPost().getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 다른 분과 교환이 진행 중인 글입니다.");
        }

        LocalDateTime now = LocalDateTime.now(SEOUL).truncatedTo(ChronoUnit.SECONDS);
        TradeAppointment appointment;
        try {
            appointment = appointmentRepository.saveAndFlush(
                    new TradeAppointment(room, userId, room.getOpponentId(userId), meetingPlace.strip(), now));
        } catch (DataIntegrityViolationException e) {
            // 동시 요청으로 UK(room_id)에 걸린 경우
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 교환 약속이 확정된 채팅방입니다.");
        }

        // 확정되면 글을 예약 상태로 바꾼다.
        ExchangePost post = room.getPost();
        post.updateStatus("RESERVED");

        ChatMessage saved = chatMessageRepository.saveAndFlush(new ChatMessage(
                room, userId, ChatMessageType.APPOINTMENT,
                "교환 약속이 확정되었습니다. 장소: " + appointment.getMeetingPlace(), null));

        return new ConfirmResult(
                new AppointmentConfirmResponse(appointment.getAppointmentId(), appointment.getMeetingPlace(),
                        appointment.getConfirmedAt()),
                ChatMessageBroadcastResponse.from(saved));
    }

    /** 약속 참여자가 교환 완료를 기록하고, 교환글 상태를 COMPLETED로 바꾼다. */
    @Transactional
    public AppointmentCompleteResponse complete(Long userId, Long appointmentId) {
        TradeAppointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "교환 약속을 찾을 수 없습니다."));
        if (!appointment.isParticipant(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "교환 약속에 접근할 권한이 없습니다.");
        }
        if (appointment.isCompleted()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 교환이 완료된 약속입니다.");
        }

        appointment.complete(LocalDateTime.now(SEOUL).truncatedTo(ChronoUnit.SECONDS));
        appointment.getPost().updateStatus("COMPLETED");

        return new AppointmentCompleteResponse(appointment.getAppointmentId(), appointment.getCompletedAt());
    }

    /** 내 약속과 교환 내역을 확정 시각 최신순으로 조회한다. status: CONFIRMED(진행 중) / COMPLETED(완료), 없으면 전체. */
    public AppointmentListResponse getMyAppointments(Long userId, String status, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "confirmedAt"));

        Page<TradeAppointment> result;
        if (status == null || status.isBlank()) {
            result = appointmentRepository.findMine(userId, pageable);
        } else if ("CONFIRMED".equals(status)) {
            result = appointmentRepository.findMineConfirmed(userId, pageable);
        } else if ("COMPLETED".equals(status)) {
            result = appointmentRepository.findMineCompleted(userId, pageable);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "status는 CONFIRMED 또는 COMPLETED여야 합니다.");
        }

        var content = result.getContent().stream()
                .map(appointment -> {
                    Long opponentId = appointment.getOpponentId(userId);
                    User opponent = userRepository.findById(opponentId).orElse(null);
                    boolean reviewed = reviewRepository.existsByAppointmentAndReviewerId(appointment, userId);
                    return AppointmentListResponse.Item.of(appointment, opponentId, opponent, reviewed);
                })
                .toList();

        return new AppointmentListResponse(content, result.getNumber(), result.getSize());
    }
}
