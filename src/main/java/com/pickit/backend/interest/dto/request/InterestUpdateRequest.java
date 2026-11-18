package com.pickit.backend.interest.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.Set;

/** 선택한 관심사 전체를 보낸다. 빈 배열이면 관심사를 모두 해제한다. */
public record InterestUpdateRequest(
        @NotNull Set<@NotNull @Positive Long> categoryIds
) {
}
