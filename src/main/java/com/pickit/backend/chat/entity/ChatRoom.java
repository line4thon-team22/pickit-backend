package com.pickit.backend.chat.entity;

import com.pickit.backend.post.entity.ExchangePost;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "chat_rooms", uniqueConstraints = {
        @UniqueConstraint(name = "uk_chat_rooms_post_buyer", columnNames = {"post_id", "buyer_id"})
})
@EntityListeners(AuditingEntityListener.class)
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long roomId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private ExchangePost post;

    /** 게시글 작성자 (users.user_id 참조) */
    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    /** 교환 신청자 (users.user_id 참조) */
    @Column(name = "buyer_id", nullable = false)
    private Long buyerId;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected ChatRoom() {
    }

    public ChatRoom(ExchangePost post, Long sellerId, Long buyerId) {
        this.post = post;
        this.sellerId = sellerId;
        this.buyerId = buyerId;
    }

    public boolean isParticipant(Long userId) {
        return sellerId.equals(userId) || buyerId.equals(userId);
    }

    public Long getOpponentId(Long userId) {
        return sellerId.equals(userId) ? buyerId : sellerId;
    }

    public Long getRoomId() { return roomId; }
    public ExchangePost getPost() { return post; }
    public Long getSellerId() { return sellerId; }
    public Long getBuyerId() { return buyerId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
