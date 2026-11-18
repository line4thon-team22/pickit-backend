package com.pickit.backend.post.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "post_likes", uniqueConstraints = {
        @UniqueConstraint(name = "uk_post_likes_user_post", columnNames = {"user_id", "post_id"})
})
public class PostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Long likeId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private ExchangePost exchangePost;

    protected PostLike() {
    }

    public PostLike(Long userId, ExchangePost exchangePost) {
        this.userId = userId;
        this.exchangePost = exchangePost;
    }

    public Long getLikeId() { return likeId; }
    public Long getUserId() { return userId; }
    public ExchangePost getExchangePost() { return exchangePost; }
}
