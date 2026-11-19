package com.pickit.backend.verification.dto.response;

import java.time.OffsetDateTime;

/** 앱 전체 위치 인증 상태. 한 번이라도 위치 인증을 완료했으면 계속 true다. 인증 전이면 두 시각은 null이다. */
public record LocationStatusResponse(
        boolean isLocationVerified,
        OffsetDateTime firstVerifiedAt,
        OffsetDateTime lastVerifiedAt
) {
}
