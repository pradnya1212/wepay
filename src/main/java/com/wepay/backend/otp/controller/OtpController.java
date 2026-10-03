package com.wepay.backend.otp.controller;

import com.wepay.backend.otp.dto.OtpRequest;
import com.wepay.backend.otp.dto.OtpVerifyRequest;
import com.wepay.backend.otp.service.OtpService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/otp")
public class OtpController {

    private final OtpService otpService;

    public OtpController(
            OtpService otpService
    ) {
        this.otpService = otpService;
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generateOtp(
            @Valid @RequestBody OtpRequest request
    ) {

        String otp =
                otpService.generateOtp(
                        request.getUserId()
                );

        return ResponseEntity.ok(
                java.util.Map.of(
                        "message",
                        "OTP generated successfully",
                        "otp",
                        otp
                )
        );
    }

    @PostMapping("/verify/{userId}")
    public ResponseEntity<?> verifyOtp(
            @PathVariable Long userId,
            @Valid @RequestBody OtpVerifyRequest request
    ) {

        otpService.verifyOtp(
                userId,
                request
        );

        return ResponseEntity.ok(
                java.util.Map.of(
                        "message",
                        "OTP verified successfully"
                )
        );
    }
}