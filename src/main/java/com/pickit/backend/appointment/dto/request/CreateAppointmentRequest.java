package com.pickit.backend.appointment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateAppointmentRequest(
        @NotBlank(message = "교환 장소는 필수입니다.")
        @Size(max = 100, message = "교환 장소는 100자 이하여야 합니다.")
        String meetingPlace
) {
}
