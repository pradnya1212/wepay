package com.wepay.backend.transaction.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RefundResponse {

    private Long refundId;
    private Long transactionId;
    private BigDecimal amount;
    private String status;
    private LocalDateTime createdAt;
    private String message;

    public RefundResponse(
            Long refundId,
            Long transactionId,
            BigDecimal amount,
            String status,
            LocalDateTime createdAt,
            String message
    ) {
        this.refundId = refundId;
        this.transactionId = transactionId;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
        this.message = message;
    }

    public Long getRefundId() {
        return refundId;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getMessage() {
        return message;
    }
}