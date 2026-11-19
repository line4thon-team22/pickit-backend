package com.pickit.backend.post.entity;

import com.pickit.backend.category.entity.Category;
import com.pickit.backend.popup.entity.Popup;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "exchange_posts")
@EntityListeners(AuditingEntityListener.class)
public class ExchangePost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long postId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "popup_id", nullable = false)
    private Popup popup;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "have_item_name", nullable = false, length = 100)
    private String haveItemName;

    @Column(name = "want_item_name", nullable = false, length = 100)
    private String wantItemName;

    @Column(name = "preferred_location", length = 100)
    private String preferredLocation;

    @Column(name = "preferred_time", length = 100)
    private String preferredTime;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "OPEN"; // OPEN, RESERVED, COMPLETED

    // 글 작성 시점의 위치 인증 기록 id. 없으면(null) 처음부터 일반 교환글.
    @Column(name = "verification_id")
    private Long verificationId;

    @OneToMany(mappedBy = "exchangePost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImage> images = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected ExchangePost() {
    }

    public ExchangePost(Long userId, Popup popup, Category category, String haveItemName, String wantItemName,
                        String preferredLocation, String preferredTime, String description) {
        this.userId = userId;
        this.popup = popup;
        this.category = category;
        this.haveItemName = haveItemName;
        this.wantItemName = wantItemName;
        this.preferredLocation = preferredLocation;
        this.preferredTime = preferredTime;
        this.description = description;
    }

    public void linkVerification(Long verificationId) {
        this.verificationId = verificationId;
    }

    public Long getVerificationId() {
        return verificationId;
    }

    public void update(String wantItemName, String preferredLocation, String preferredTime, String description) {
        if (wantItemName != null) this.wantItemName = wantItemName;
        if (preferredLocation != null) this.preferredLocation = preferredLocation;
        if (preferredTime != null) this.preferredTime = preferredTime;
        if (description != null) this.description = description;
    }

    public void updateStatus(String status) {
        this.status = status;
    }

    public void addImage(PostImage image) {
        this.images.add(image);
        image.setExchangePost(this);
    }

    public void clearImages() {
        this.images.clear();
    }

    public Long getPostId() { return postId; }
    public Long getUserId() { return userId; }
    public Popup getPopup() { return popup; }
    public Category getCategory() { return category; }
    public String getHaveItemName() { return haveItemName; }
    public String getWantItemName() { return wantItemName; }
    public String getPreferredLocation() { return preferredLocation; }
    public String getPreferredTime() { return preferredTime; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public List<PostImage> getImages() { return images; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
