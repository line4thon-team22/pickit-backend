package com.pickit.backend.popup.entity;

import com.pickit.backend.category.entity.Category;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "popups")
public class Popup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "popup_id")
    private Long popupId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "category_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_popups_category")
    )
    private Category category;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "district", nullable = false, length = 30)
    private String district;

    @Column(name = "address", nullable = false, length = 255)
    private String address;

    @Column(name = "latitude", nullable = false, precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(name = "longitude", nullable = false, precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;

    protected Popup() {
    }

    public Popup(
            Category category,
            String name,
            String description,
            String district,
            String address,
            BigDecimal latitude,
            BigDecimal longitude,
            LocalDate startDate,
            LocalDate endDate,
            String status,
            String thumbnailUrl
    ) {
        this.category = category;
        this.name = name;
        this.description = description;
        this.district = district;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.thumbnailUrl = thumbnailUrl;
    }

    public Long getPopupId() {
        return popupId;
    }

    public Category getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getDistrict() {
        return district;
    }

    public String getAddress() {
        return address;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getStatus() {
        return status;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }
}
