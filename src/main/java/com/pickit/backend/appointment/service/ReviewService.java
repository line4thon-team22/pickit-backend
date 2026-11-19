package com.pickit.backend.appointment.service;

import com.pickit.backend.appointment.dto.response.ReviewResponse;
import com.pickit.backend.appointment.entity.TradeAppointment;
import com.pickit.backend.appointment.entity.TradeReview;
import com.pickit.backend.appointment.repository.TradeAppointmentRepository;
import com.pickit.backend.appointment.repository.TradeReviewRepository;
import com.pickit.backend.auth.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class ReviewService {

    private final TradeAppointmentRepository appointmentRepository;
    private final TradeReviewRepository reviewRepository;
    private final UserRepository userRepository;

    public ReviewService(TradeAppointmentRepository appointmentRepository,
                         TradeReviewRepository reviewRepository,
                         UserRepository userRepository) {
        this.appointmentRepository = appointmentRepository;
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
    }

    /** 교환이 완료된 약속의 참여자가 상대방에게 별점(1~5)을 남기고, 상대방의 매너 점수를 갱신한다. */
    @Transactional
    public ReviewResponse createReview(Long userId, Long appointmentId, int rating) {
        TradeAppointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "교환 약속을 찾을 수 없습니다."));
        if (!appointment.isParticipant(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "교환 약속에 접근할 권한이 없습니다.");
        }
        if (!appointment.isCompleted()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "교환이 완료된 후에 평가할 수 있습니다.");
        }
        if (reviewRepository.existsByAppointmentAndReviewerId(appointment, userId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 평가를 남기셨습니다.");
        }

        Long revieweeId = appointment.getOpponentId(userId);
        TradeReview review;
        try {
            review = reviewRepository.saveAndFlush(new TradeReview(appointment, userId, revieweeId, rating));
        } catch (DataIntegrityViolationException e) {
            // 동시 요청으로 UK(appointment_id, reviewer_id)에 걸린 경우
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 평가를 남기셨습니다.");
        }

        // 받은 별점의 평균(소수 첫째 자리)을 매너 점수로 저장한다.
        Double average = reviewRepository.findAverageRatingByRevieweeId(revieweeId);
        if (average != null) {
            userRepository.findById(revieweeId)
                    .ifPresent(user -> user.updateMannerScore(Math.round(average * 10) / 10.0));
        }

        return new ReviewResponse(review.getReviewId(), appointment.getAppointmentId(), review.getRating(),
                review.getCreatedAt());
    }
}
