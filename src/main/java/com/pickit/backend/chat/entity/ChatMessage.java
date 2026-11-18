package com.pickit.backend.chat.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "chat_messages")
@EntityListeners(AuditingEntityListener.class)
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long messageId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private ChatRoom room;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false, length = 20)
    private ChatMessageType messageType;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected ChatMessage() {
    }

    public ChatMessage(ChatRoom room, Long senderId, ChatMessageType messageType, String content, String imageUrl) {
        this.room = room;
        this.senderId = senderId;
        this.messageType = messageType;
        this.content = content;
        this.imageUrl = imageUrl;
    }

    public Long getMessageId() { return messageId; }
    public ChatRoom getRoom() { return room; }
    public Long getSenderId() { return senderId; }
    public ChatMessageType getMessageType() { return messageType; }
    public String getContent() { return content; }
    public String getImageUrl() { return imageUrl; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
