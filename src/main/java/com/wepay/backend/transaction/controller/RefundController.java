package com.wepay.backend.transaction.controller;

import com.wepay.backend.transaction.dto.RefundRequest;
import com.wepay.backend.transaction.dto.RefundResponse;
import com.wepay.backend.transaction.entity.Refund;
import com.wepay.backend.transaction.service.RefundService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/refunds")
public class RefundController {

    private final RefundService refundService;

    public RefundController(
            RefundService refundService
    ) {
        this.refundService = refundService;
    }

    @PostMapping("/{transactionId}")
    public ResponseEntity<RefundResponse> requestRefund(
            Authentication authentication,
            @PathVariable Long transactionId,
            @Valid @RequestBody RefundRequest request
    ) {

        Long userId =
                (Long) authentication.getPrincipal();

        Refund refund =
                refundService.requestRefund(
                        userId,
                        transactionId,
                        request
                );

        RefundResponse response =
                new RefundResponse(
                        refund.getId(),
                        refund.getTransactionId(),
                        refund.getAmount(),
                        refund.getStatus().name(),
                        refund.getCreatedAt(),
                        "Refund processed successfully"
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}