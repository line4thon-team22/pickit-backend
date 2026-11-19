package com.pickit.backend.appointment.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/** 교환 완료 후 상대방에게 남기는 별점(1~5). 약속 하나당 참여자마다 한 번씩 남길 수 있다. */
@Entity
@Table(name = "trade_reviews", uniqueConstraints = {
        @UniqueConstraint(name = "uk_trade_reviews_appointment_reviewer", columnNames = {"appointment_id", "reviewer_id"})
})
@EntityListeners(AuditingEntityListener.class)
public class TradeReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "appointment_id", nullable = false)
    private TradeAppointment appointment;

    /** 평가한 사람 (users.user_id 참조) */
    @Column(name = "reviewer_id", nullable = false)
    private Long reviewerId;

    /** 평가받은 사람 (users.user_id 참조) */
    @Column(name = "reviewee_id", nullable = false)
    private Long revieweeId;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected TradeReview() {
    }

    public TradeReview(TradeAppointment appointment, Long reviewerId, Long revieweeId, Integer rating) {
        this.appointment = appointment;
        this.reviewerId = reviewerId;
        this.revieweeId = revieweeId;
        this.rating = rating;
    }

    public Long getReviewId() { return reviewId; }
    public TradeAppointment getAppointment() { return appointment; }
    public Long getReviewerId() { return reviewerId; }
    public Long getRevieweeId() { return revieweeId; }
    public Integer getRating() { return rating; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
