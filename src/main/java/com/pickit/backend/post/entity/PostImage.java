package com.pickit.backend.post.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "post_images")
public class PostImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long imageId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private ExchangePost exchangePost;

    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    protected PostImage() {
    }

    public PostImage(String imageUrl, Integer sortOrder) {
        this.imageUrl = imageUrl;
        this.sortOrder = sortOrder;
    }

    public void setExchangePost(ExchangePost exchangePost) {
        this.exchangePost = exchangePost;
    }

    public Long getImageId() { return imageId; }
    public ExchangePost getExchangePost() { return exchangePost; }
    public String getImageUrl() { return imageUrl; }
    public Integer getSortOrder() { return sortOrder; }
}
