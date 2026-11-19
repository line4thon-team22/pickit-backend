package com.pickit.backend.verification.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;

/**
 * 회원이 위치 인증(확인 버튼)을 한 번이라도 완료했는지 기록한다. 회원당 한 행이며 만료 없이 유지한다.
 * 반경 안에 팝업이 없어도 위치 측정이 정상이면 기록한다. 팝업별 인증·유효기간은 popup_verifications가 맡는다.
 */
// 최종 ERD에 없는 테이블이다. popup_verifications.popup_id가 NOT NULL이라 팝업 없는 인증을 담을 수 없어 추가한다(#59).
@Entity
@Table(name = "user_location_verifications", uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_location_verifications_user", columnNames = "user_id")
})
public class UserLocationVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_verification_id")
    private Long locationVerificationId;

    // 회원 엔티티는 로그인 담당자가 관리한다. users FK는 수동 DDL로 연결한다.
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "first_verified_at", nullable = false)
    private LocalDateTime firstVerifiedAt;

    @Column(name = "last_verified_at", nullable = false)
    private LocalDateTime lastVerifiedAt;

    protected UserLocationVerification() {
    }

    public UserLocationVerification(Long userId, LocalDateTime verifiedAt) {
        this.userId = userId;
        this.firstVerifiedAt = verifiedAt;
        this.lastVerifiedAt = verifiedAt;
    }

    /** 최초 인증 시각은 그대로 두고 마지막 인증 시각만 갱신한다. */
    public void verifyAgainAt(LocalDateTime verifiedAt) {
        if (verifiedAt.isAfter(lastVerifiedAt)) {
            lastVerifiedAt = verifiedAt;
        }
    }

    public Long getLocationVerificationId() { return locationVerificationId; }
    public Long getUserId() { return userId; }
    public LocalDateTime getFirstVerifiedAt() { return firstVerifiedAt; }
    public LocalDateTime getLastVerifiedAt() { return lastVerifiedAt; }
}
