package com.pickit.backend.appointment.dto.response;

import java.time.LocalDateTime;

public record AppointmentConfirmResponse(
        Long appointmentId,
        String meetingPlace,
        LocalDateTime confirmedAt
) {
}
