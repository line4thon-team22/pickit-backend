package com.pickit.backend.appointment.dto.response;

import com.pickit.backend.appointment.entity.TradeAppointment;
import com.pickit.backend.auth.entity.User;
import com.pickit.backend.chat.dto.response.ChatRoomDetailResponse;
import java.time.LocalDateTime;
import java.util.List;

public record AppointmentListResponse(
        List<Item> content,
        int page,
        int size
) {
    public record Item(
            Long appointmentId,
            ChatRoomDetailResponse.PostInfo post,
            ChatRoomDetailResponse.Opponent opponent,
            String meetingPlace,
            LocalDateTime confirmedAt,
            LocalDateTime completedAt
    ) {
        public static Item of(TradeAppointment appointment, Long opponentId, User opponent) {
            return new Item(
                    appointment.getAppointmentId(),
                    ChatRoomDetailResponse.PostInfo.from(appointment.getPost()),
                    ChatRoomDetailResponse.Opponent.from(opponentId, opponent),
                    appointment.getMeetingPlace(),
                    appointment.getConfirmedAt(),
                    appointment.getCompletedAt()
            );
        }
    }
}
