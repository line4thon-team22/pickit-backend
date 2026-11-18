package com.pickit.backend.post.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdatePostStatusRequest(
        @NotBlank(message = "status는 필수입니다.")
        @Pattern(regexp = "OPEN|RESERVED|COMPLETED", message = "status는 OPEN, RESERVED, COMPLETED 중 하나여야 합니다.")
        String status
) {
}
