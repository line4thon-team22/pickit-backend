package com.pickit.backend.auth.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "manner_score", nullable = false)
    private Double mannerScore = 5.0; // 받은 별점(1~5) 평균, 평가가 없으면 5.0

    @Column(name = "is_premium", nullable = false)
    private Boolean isPremium = false;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected User() {
    }

    public User(String username) {
        this.username = username;
    }

    public void updateUsername(String username) {
        this.username = username;
    }

    public void updateProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void updateMannerScore(Double mannerScore) {
        this.mannerScore = mannerScore;
    }

    public void clearProfileImageUrl() {
        this.profileImageUrl = null;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public Double getMannerScore() {
        return mannerScore;
    }

    public Boolean getIsPremium() {
        return isPremium;
    }
}
