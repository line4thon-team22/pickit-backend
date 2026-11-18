package com.pickit.backend.appointment.dto.response;

import java.time.LocalDateTime;

public record AppointmentCompleteResponse(
        Long appointmentId,
        LocalDateTime completedAt
) {
}
