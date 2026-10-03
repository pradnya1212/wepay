package com.wepay.backend.pin.repository;

import com.wepay.backend.pin.entity.PaymentPin;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentPinRepository
        extends JpaRepository<PaymentPin, Long> {

    Optional<PaymentPin> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}