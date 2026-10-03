package com.wepay.backend.pin.service;

import com.wepay.backend.pin.entity.PaymentPin;
import com.wepay.backend.pin.repository.PaymentPinRepository;
import com.wepay.backend.pin.dto.PaymentPinRequest;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PaymentPinService {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCK_DURATION_MINUTES = 24 * 60;

    private final PaymentPinRepository paymentPinRepository;
    private final PasswordEncoder passwordEncoder;

    public PaymentPinService(
            PaymentPinRepository paymentPinRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.paymentPinRepository = paymentPinRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void setPin(Long userId, PaymentPinRequest request) {

        PaymentPin paymentPin =
                paymentPinRepository
                        .findByUserId(userId)
                        .orElse(null);

        String pinHash =
                passwordEncoder.encode(request.getPin());

        if (paymentPin == null) {

            paymentPin =
                    new PaymentPin(userId, pinHash);

        } else {

            paymentPin.setPinHash(pinHash);
            paymentPin.setFailedAttempts(0);
            paymentPin.setLockedUntil(null);
        }

        paymentPinRepository.save(paymentPin);
    }

    @Transactional
    public boolean verifyPin(
            Long userId,
            String pin
    ) {

        PaymentPin paymentPin =
                paymentPinRepository
                        .findByUserId(userId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Payment PIN not set"
                                )
                        );

        // Check whether PIN is locked
        if (isLocked(paymentPin)) {

            throw new RuntimeException(
                    "Payment PIN temporarily locked"
            );
        }

        boolean matches =
                passwordEncoder.matches(
                        pin,
                        paymentPin.getPinHash()
                );

        if (!matches) {

            handleFailedAttempt(paymentPin);

            return false;
        }

        // Correct PIN
        paymentPin.setFailedAttempts(0);
        paymentPin.setLockedUntil(null);

        paymentPinRepository.save(paymentPin);

        return true;
    }

    private boolean isLocked(PaymentPin paymentPin) {

        LocalDateTime lockedUntil =
                paymentPin.getLockedUntil();

        if (lockedUntil == null) {
            return false;
        }

        if (LocalDateTime.now().isAfter(lockedUntil)) {

            paymentPin.setLockedUntil(null);
            paymentPin.setFailedAttempts(0);

            paymentPinRepository.save(paymentPin);

            return false;
        }

        return true;
    }

    private void handleFailedAttempt(
            PaymentPin paymentPin
    ) {

        int attempts =
                paymentPin.getFailedAttempts() + 1;

        paymentPin.setFailedAttempts(attempts);

        if (attempts >= MAX_FAILED_ATTEMPTS) {

            LocalDateTime lockUntil =
                    LocalDateTime.now()
                            .plusMinutes(
                                    LOCK_DURATION_MINUTES
                            );

            paymentPin.setLockedUntil(lockUntil);

            paymentPin.setFailedAttempts(0);
        }

        paymentPinRepository.save(paymentPin);
    }
}