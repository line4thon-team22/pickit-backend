package com.pickit.backend.appointment.repository;

import com.pickit.backend.appointment.entity.TradeAppointment;
import com.pickit.backend.chat.entity.ChatRoom;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TradeAppointmentRepository extends JpaRepository<TradeAppointment, Long> {

    Optional<TradeAppointment> findByRoom(ChatRoom room);

    boolean existsByRoom(ChatRoom room);

    @Query("select a from TradeAppointment a where a.userId = :userId or a.userId2 = :userId")
    Page<TradeAppointment> findMine(@Param("userId") Long userId, Pageable pageable);

    /** 확정만 되고 아직 교환이 완료되지 않은 약속 */
    @Query("select a from TradeAppointment a where (a.userId = :userId or a.userId2 = :userId) and a.completedAt is null")
    Page<TradeAppointment> findMineConfirmed(@Param("userId") Long userId, Pageable pageable);

    @Query("select a from TradeAppointment a where (a.userId = :userId or a.userId2 = :userId) and a.completedAt is not null")
    Page<TradeAppointment> findMineCompleted(@Param("userId") Long userId, Pageable pageable);
}
