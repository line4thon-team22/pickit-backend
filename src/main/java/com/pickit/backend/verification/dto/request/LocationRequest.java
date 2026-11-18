package com.pickit.backend.verification.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.Instant;

public record LocationRequest(
        @NotNull @DecimalMin("-90") @DecimalMax("90") BigDecimal lat,
        // MySQL ST_Distance_Sphere는 경도 -180을 받지 않으므로 입력에서 제외한다.
        @NotNull @DecimalMin(value = "-180", inclusive = false) @DecimalMax("180") BigDecimal lng,
        @NotNull @Positive BigDecimal accuracyMeters,
        @NotNull Instant measuredAt
) {
}
