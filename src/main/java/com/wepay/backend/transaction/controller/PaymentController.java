package com.wepay.backend.transaction.controller;

import com.wepay.backend.transaction.dto.PaymentRequest;
import com.wepay.backend.transaction.dto.PaymentResponse;
import com.wepay.backend.transaction.entity.Transaction;
import com.wepay.backend.transaction.service.PaymentService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(
            PaymentService paymentService
    ) {
        this.paymentService = paymentService;
    }

    @PostMapping("/send")
    public ResponseEntity<PaymentResponse> sendMoney(
            Authentication authentication,
            @Valid @RequestBody PaymentRequest request
    ) {

        Long senderUserId =
                (Long) authentication.getPrincipal();

        Transaction transaction =
                paymentService.sendMoney(
                        senderUserId,
                        request
                );

        String message;

        if (transaction.getStatus().name().equals("SUCCESS")) {
            message = "Payment successful";
        } else {
            message = "Payment failed";
        }

        PaymentResponse response =
                new PaymentResponse(
                        transaction.getId(),
                        transaction.getTransactionReference(),
                        transaction.getSenderUserId(),
                        transaction.getReceiverUserId(),
                        transaction.getAmount(),
                        transaction.getStatus().name(),
                        transaction.getCreatedAt(),
                        message
                );

        return ResponseEntity.ok(response);
    }
}