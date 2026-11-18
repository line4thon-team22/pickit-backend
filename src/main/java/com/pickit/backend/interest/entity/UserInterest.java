package com.pickit.backend.interest.entity;

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
import jakarta.persistence.UniqueConstraint;

// 최종 ERD의 user_interests 3개 컬럼을 사용한다. 중복 선택을 막는 UK는 ERD에 없어 추가한다.
@Entity
@Table(name = "user_interests", uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_interests_user_category", columnNames = {"user_id", "category_id"})
})
public class UserInterest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_interest_id")
    private Long userInterestId;

    // 회원 엔티티는 로그인 담당자가 관리한다. users FK는 수동 DDL로 연결한다.
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_user_interests_category"))
    private Category category;

    protected UserInterest() {
    }

    public UserInterest(Long userId, Category category) {
        this.userId = userId;
        this.category = category;
    }

    public Long getUserInterestId() {
        return userInterestId;
    }

    public Long getUserId() {
        return userId;
    }

    public Category getCategory() {
        return category;
    }
}
