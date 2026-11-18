package com.pickit.backend.appointment.entity;

import com.pickit.backend.chat.entity.ChatRoom;
import com.pickit.backend.post.entity.ExchangePost;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "trade_appointments", uniqueConstraints = {
        @UniqueConstraint(name = "uk_trade_appointments_room", columnNames = {"room_id"})
})
public class TradeAppointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appointment_id")
    private Long appointmentId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private ChatRoom room;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private ExchangePost post;

    /** 약속을 확정한 사람 (users.user_id 참조) */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** 상대방 (users.user_id 참조) */
    @Column(name = "user_id2", nullable = false)
    private Long userId2;

    @Column(name = "meeting_place", nullable = false, length = 100)
    private String meetingPlace;

    @Column(name = "confirmed_at", nullable = false)
    private LocalDateTime confirmedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    protected TradeAppointment() {
    }

    public TradeAppointment(ChatRoom room, Long userId, Long userId2, String meetingPlace, LocalDateTime confirmedAt) {
        this.room = room;
        this.post = room.getPost();
        this.userId = userId;
        this.userId2 = userId2;
        this.meetingPlace = meetingPlace;
        this.confirmedAt = confirmedAt;
    }

    public boolean isCompleted() {
        return completedAt != null;
    }

    public void complete(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public boolean isParticipant(Long userId) {
        return this.userId.equals(userId) || this.userId2.equals(userId);
    }

    public Long getOpponentId(Long userId) {
        return this.userId.equals(userId) ? userId2 : this.userId;
    }

    public Long getAppointmentId() { return appointmentId; }
    public ChatRoom getRoom() { return room; }
    public ExchangePost getPost() { return post; }
    public Long getUserId() { return userId; }
    public Long getUserId2() { return userId2; }
    public String getMeetingPlace() { return meetingPlace; }
    public LocalDateTime getConfirmedAt() { return confirmedAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
}
