package com.wepay.backend.otp.service;

import com.wepay.backend.otp.dto.OtpVerifyRequest;
import com.wepay.backend.otp.entity.OtpVerification;
import com.wepay.backend.otp.repository.OtpVerificationRepository;
import com.wepay.backend.user.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class OtpService {

    private static final int OTP_EXPIRY_MINUTES = 5;

    private static final int MAX_FAILED_ATTEMPTS = 5;

    private static final long LOCK_DURATION_MINUTES = 24L * 60L;

    private static final long RESEND_COOLDOWN_SECONDS = 60L;

    private final OtpVerificationRepository otpRepository;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final SecureRandom secureRandom = new SecureRandom();

    public OtpService(
            OtpVerificationRepository otpRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.otpRepository = otpRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /*
     * Generate OTP
     */
    @Transactional
    public String generateOtp(Long userId) {

        // Check user exists
        if (!userRepository.existsById(userId)) {

            throw new RuntimeException(
                    "User not found"
            );
        }

        /*
         * Find the latest OTP generated for this user.
         */
        OtpVerification latestOtp =
                otpRepository
                        .findTopByUserIdOrderByCreatedAtDesc(
                                userId
                        )
                        .orElse(null);

        /*
         * Resend protection.
         *
         * User must wait 60 seconds before
         * requesting another OTP.
         */
        if (latestOtp != null) {

            LocalDateTime nextAllowedTime =
                    latestOtp.getCreatedAt()
                            .plusSeconds(
                                    RESEND_COOLDOWN_SECONDS
                            );

            if (LocalDateTime.now()
                    .isBefore(nextAllowedTime)) {

                throw new RuntimeException(
                        "Please wait before requesting another OTP"
                );
            }
        }

        /*
         * Generate secure 6-digit OTP.
         */
        String otp =
                generateSixDigitOtp();

        /*
         * Never store plain OTP in database.
         * Store BCrypt hash instead.
         */
        String otpHash =
                passwordEncoder.encode(otp);

        /*
         * OTP expires after 5 minutes.
         */
        LocalDateTime expiresAt =
                LocalDateTime.now()
                        .plusMinutes(
                                OTP_EXPIRY_MINUTES
                        );

        OtpVerification otpVerification =
                new OtpVerification(
                        userId,
                        otpHash,
                        expiresAt
                );

        otpRepository.save(
                otpVerification
        );

        /*
         * DEVELOPMENT / TESTING ONLY
         *
         * In production:
         * OTP should be sent through an
         * authorized SMS/email provider.
         *
         * OTP must NOT be returned in API response.
         */
        return otp;
    }

    /*
     * Verify OTP
     */
    @Transactional
    public boolean verifyOtp(
            Long userId,
            OtpVerifyRequest request
    ) {

        OtpVerification otpVerification =
                otpRepository
                        .findTopByUserIdOrderByCreatedAtDesc(
                                userId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "OTP not found"
                                )
                        );

        /*
         * OTP already used?
         */
        if (otpVerification.isVerified()) {

            throw new RuntimeException(
                    "OTP already used"
            );
        }

        /*
         * Check whether OTP verification
         * is currently locked.
         */
        if (isLocked(otpVerification)) {

            throw new RuntimeException(
                    "OTP verification temporarily locked"
            );
        }

        /*
         * Check OTP expiry.
         */
        if (LocalDateTime.now()
                .isAfter(
                        otpVerification.getExpiresAt()
                )) {

            throw new RuntimeException(
                    "OTP expired"
            );
        }

        /*
         * Compare submitted OTP with
         * BCrypt hash stored in database.
         */
        boolean matches =
                passwordEncoder.matches(
                        request.getOtp(),
                        otpVerification.getOtpHash()
                );

        /*
         * Wrong OTP
         */
        if (!matches) {

            handleFailedAttempt(
                    otpVerification
            );

            throw new RuntimeException(
                    "Invalid OTP"
            );
        }

        /*
         * Correct OTP
         *
         * Mark as verified so that the
         * same OTP cannot be reused.
         */
        otpVerification.setVerified(true);

        otpVerification.setFailedAttempts(0);

        otpVerification.setLockedUntil(null);

        otpRepository.save(
                otpVerification
        );

        return true;
    }

    /*
     * Generate secure 6-digit OTP.
     */
    private String generateSixDigitOtp() {

        int otp =
                secureRandom.nextInt(
                        1_000_000
                );

        return String.format(
                "%06d",
                otp
        );
    }

    /*
     * Check OTP lock.
     */
    private boolean isLocked(
            OtpVerification otpVerification
    ) {

        LocalDateTime lockedUntil =
                otpVerification.getLockedUntil();

        /*
         * Not locked.
         */
        if (lockedUntil == null) {

            return false;
        }

        /*
         * Lock expired.
         */
        if (LocalDateTime.now()
                .isAfter(lockedUntil)) {

            otpVerification.setLockedUntil(
                    null
            );

            otpVerification.setFailedAttempts(
                    0
            );

            otpRepository.save(
                    otpVerification
            );

            return false;
        }

        /*
         * Still locked.
         */
        return true;
    }

    /*
     * Handle incorrect OTP attempt.
     */
    private void handleFailedAttempt(
            OtpVerification otpVerification
    ) {

        int attempts =
                otpVerification
                        .getFailedAttempts()
                        + 1;

        otpVerification.setFailedAttempts(
                attempts
        );

        /*
         * 5 wrong attempts
         * → 24-hour lock.
         */
        if (attempts >= MAX_FAILED_ATTEMPTS) {

            LocalDateTime lockUntil =
                    LocalDateTime.now()
                            .plusMinutes(
                                    LOCK_DURATION_MINUTES
                            );

            otpVerification.setLockedUntil(
                    lockUntil
            );

            otpVerification.setFailedAttempts(
                    MAX_FAILED_ATTEMPTS
            );
        }

        otpRepository.save(
                otpVerification
        );
    }
}