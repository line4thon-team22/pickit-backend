package com.pickit.backend.verification.repository;

import com.pickit.backend.verification.entity.UserLocationVerification;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLocationVerificationRepository extends JpaRepository<UserLocationVerification, Long> {

    Optional<UserLocationVerification> findByUserId(Long userId);
}
