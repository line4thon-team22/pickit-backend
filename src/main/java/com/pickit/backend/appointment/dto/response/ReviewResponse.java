package com.pickit.backend.appointment.dto.response;

import java.time.LocalDateTime;

public record ReviewResponse(Long reviewId, Long appointmentId, Integer rating, LocalDateTime createdAt) {
}
