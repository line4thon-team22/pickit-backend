package com.pickit.backend.chat.dto.response;

import com.pickit.backend.appointment.entity.TradeAppointment;
import com.pickit.backend.auth.entity.User;
import com.pickit.backend.chat.entity.ChatRoom;
import com.pickit.backend.post.entity.ExchangePost;
import com.pickit.backend.post.entity.PostImage;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public record ChatRoomDetailResponse(
        Long roomId,
        PostInfo post,
        Opponent opponent,
        AppointmentInfo appointment
) {
    public record PostInfo(
            Long postId,
            Long userId,
            Long popupId,
            Long categoryId,
            String haveItemName,
            String wantItemName,
            String preferredLocation,
            String preferredTime,
            String description,
            String status,
            List<String> images,
            LocalDateTime createdAt
    ) {
        public static PostInfo from(ExchangePost post) {
            List<String> images = post.getImages().stream()
                    .sorted(Comparator.comparing(PostImage::getSortOrder))
                    .map(PostImage::getImageUrl)
                    .toList();
            return new PostInfo(
                    post.getPostId(),
                    post.getUserId(),
                    post.getPopup().getPopupId(),
                    post.getCategory().getCategoryId(),
                    post.getHaveItemName(),
                    post.getWantItemName(),
                    post.getPreferredLocation(),
                    post.getPreferredTime(),
                    post.getDescription(),
                    post.getStatus(),
                    images,
                    post.getCreatedAt()
            );
        }
    }

    public record Opponent(Long userId, String username, String profileImageUrl, Double mannerScore, Boolean isPremium) {
        public static Opponent from(Long userId, User user) {
            if (user == null) {
                return new Opponent(userId, null, null, null, null);
            }
            return new Opponent(user.getUserId(), user.getUsername(), user.getProfileImageUrl(),
                    user.getMannerScore(), user.getIsPremium());
        }
    }

    /** 확정된 교환 약속. completedAt이 null이면 아직 교환 전이다. */
    public record AppointmentInfo(
            Long appointmentId,
            String meetingPlace,
            LocalDateTime confirmedAt,
            LocalDateTime completedAt
    ) {
        public static AppointmentInfo from(TradeAppointment appointment) {
            return new AppointmentInfo(
                    appointment.getAppointmentId(),
                    appointment.getMeetingPlace(),
                    appointment.getConfirmedAt(),
                    appointment.getCompletedAt()
            );
        }
    }

    /** appointment는 약속이 없으면 null이다. */
    public static ChatRoomDetailResponse of(ChatRoom room, Long opponentId, User opponent, TradeAppointment appointment) {
        return new ChatRoomDetailResponse(
                room.getRoomId(),
                PostInfo.from(room.getPost()),
                Opponent.from(opponentId, opponent),
                appointment == null ? null : AppointmentInfo.from(appointment)
        );
    }
}
