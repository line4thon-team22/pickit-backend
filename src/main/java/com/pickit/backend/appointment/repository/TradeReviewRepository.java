package com.pickit.backend.appointment.repository;

import com.pickit.backend.appointment.entity.TradeAppointment;
import com.pickit.backend.appointment.entity.TradeReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TradeReviewRepository extends JpaRepository<TradeReview, Long> {

    boolean existsByAppointmentAndReviewerId(TradeAppointment appointment, Long reviewerId);

    /** 사용자가 받은 별점의 평균. 받은 평가가 없으면 null. */
    @Query("select avg(r.rating) from TradeReview r where r.revieweeId = :userId")
    Double findAverageRatingByRevieweeId(@Param("userId") Long userId);
}
