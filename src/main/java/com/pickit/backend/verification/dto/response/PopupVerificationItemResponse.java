package com.pickit.backend.verification.dto.response;

import com.pickit.backend.popup.entity.PopupStatus;
import com.pickit.backend.verification.entity.PresenceStatus;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public record PopupVerificationItemResponse(
        Long verificationId,
        Long popupId,
        String name,
        String thumbnailUrl,
        String district,
        String address,
        LocalDate startDate,
        LocalDate endDate,
        PopupStatus popupStatus,
        long likeCount,
        // 내 현장 팝업 카드의 찜 표시. 팝업 목록의 isLiked와 같은 뜻이다.
        boolean isLiked,
        double distanceMeters,
        boolean isVerified,
        PresenceStatus presenceStatus,
        OffsetDateTime verifiedAt,
        OffsetDateTime expiredAt,
        OffsetDateTime lastLocationAt,
        OffsetDateTime leftAt
) {
}
