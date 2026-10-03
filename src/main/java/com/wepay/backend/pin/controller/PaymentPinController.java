package com.wepay.backend.pin.controller;

import com.wepay.backend.pin.dto.PaymentPinRequest;
import com.wepay.backend.pin.service.PaymentPinService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment-pin")
public class PaymentPinController {

    private final PaymentPinService paymentPinService;

    public PaymentPinController(
            PaymentPinService paymentPinService
    ) {
        this.paymentPinService = paymentPinService;
    }

    @PostMapping("/set")
    public ResponseEntity<String> setPin(
            Authentication authentication,
            @Valid @RequestBody PaymentPinRequest request
    ) {

        Long userId =
                (Long) authentication.getPrincipal();

        paymentPinService.setPin(
                userId,
                request
        );

        return ResponseEntity.ok(
                "Payment PIN set successfully"
        );
    }
}