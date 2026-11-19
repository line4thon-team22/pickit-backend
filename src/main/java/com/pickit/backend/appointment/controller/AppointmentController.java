package com.pickit.backend.appointment.controller;

import com.pickit.backend.appointment.dto.request.CreateAppointmentRequest;
import com.pickit.backend.appointment.dto.request.CreateReviewRequest;
import com.pickit.backend.appointment.dto.response.AppointmentCompleteResponse;
import com.pickit.backend.appointment.dto.response.AppointmentConfirmResponse;
import com.pickit.backend.appointment.dto.response.AppointmentListResponse;
import com.pickit.backend.appointment.dto.response.ReviewResponse;
import com.pickit.backend.appointment.service.AppointmentService;
import com.pickit.backend.appointment.service.ReviewService;
import com.pickit.backend.common.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final ReviewService reviewService;
    private final SimpMessagingTemplate messagingTemplate;

    public AppointmentController(AppointmentService appointmentService, ReviewService reviewService,
                                 SimpMessagingTemplate messagingTemplate) {
        this.appointmentService = appointmentService;
        this.reviewService = reviewService;
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping("/chat-rooms/{roomId}/appointments")
    public ApiResponse<AppointmentConfirmResponse> confirmAppointment(
            @PathVariable Long roomId,
            @Valid @RequestBody CreateAppointmentRequest request,
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();
        AppointmentService.ConfirmResult result = appointmentService.confirm(userId, roomId, request.meetingPlace());
        // 저장(커밋)이 끝난 뒤 채팅방 구독자에게 약속 확정 메시지를 전달한다.
        messagingTemplate.convertAndSend("/sub/chat-rooms/" + roomId, result.message());
        return ApiResponse.success(result.appointment());
    }

    @PatchMapping("/appointments/{appointmentId}/complete")
    public ApiResponse<AppointmentCompleteResponse> completeAppointment(
            @PathVariable Long appointmentId,
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();
        return ApiResponse.success(appointmentService.complete(userId, appointmentId));
    }

    @PostMapping("/appointments/{appointmentId}/reviews")
    public ApiResponse<ReviewResponse> createReview(
            @PathVariable Long appointmentId,
            @Valid @RequestBody CreateReviewRequest request,
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();
        return ApiResponse.success(reviewService.createReview(userId, appointmentId, request.rating()));
    }

    @GetMapping("/appointments/me")
    public ApiResponse<AppointmentListResponse> getMyAppointments(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();
        return ApiResponse.success(appointmentService.getMyAppointments(userId, status, page, size));
    }
}
