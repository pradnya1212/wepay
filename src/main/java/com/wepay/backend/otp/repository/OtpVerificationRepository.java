package com.wepay.backend.otp.repository;

import com.wepay.backend.otp.entity.OtpVerification;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpVerificationRepository
        extends JpaRepository<OtpVerification, Long> {

    Optional<OtpVerification>
    findTopByUserIdOrderByCreatedAtDesc(Long userId);
}