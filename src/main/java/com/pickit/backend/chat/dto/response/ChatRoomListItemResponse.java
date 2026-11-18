package com.pickit.backend.chat.dto.response;

import com.pickit.backend.auth.entity.User;
import com.pickit.backend.chat.entity.ChatMessage;
import com.pickit.backend.chat.entity.ChatRoom;
import java.time.LocalDateTime;

public record ChatRoomListItemResponse(
        Long roomId,
        PostSummary post,
        Opponent opponent,
        String lastMessage,
        LocalDateTime lastMessageAt
) {
    public record PostSummary(Long postId, String haveItemName, String wantItemName) {
    }

    public record Opponent(Long userId, String username, String profileImageUrl, Double mannerScore) {
        public static Opponent from(Long userId, User user) {
            if (user == null) {
                return new Opponent(userId, null, null, null);
            }
            return new Opponent(user.getUserId(), user.getUsername(), user.getProfileImageUrl(), user.getMannerScore());
        }
    }

    public static ChatRoomListItemResponse of(ChatRoom room, Long opponentId, User opponent, ChatMessage lastMessage) {
        PostSummary post = new PostSummary(
                room.getPost().getPostId(),
                room.getPost().getHaveItemName(),
                room.getPost().getWantItemName()
        );
        String preview = null;
        LocalDateTime lastAt = null;
        if (lastMessage != null) {
            preview = lastMessage.getContent() != null ? lastMessage.getContent() : "사진을 보냈습니다.";
            lastAt = lastMessage.getCreatedAt();
        }
        return new ChatRoomListItemResponse(room.getRoomId(), post, Opponent.from(opponentId, opponent), preview, lastAt);
    }
}
