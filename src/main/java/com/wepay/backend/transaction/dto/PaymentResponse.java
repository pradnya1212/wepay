package com.wepay.backend.transaction.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentResponse {

    private Long transactionId;
    private String transactionReference;

    private Long senderUserId;
    private Long receiverUserId;

    private BigDecimal amount;

    private String status;

    private LocalDateTime createdAt;

    private String message;

    public PaymentResponse(
            Long transactionId,
            String transactionReference,
            Long senderUserId,
            Long receiverUserId,
            BigDecimal amount,
            String status,
            LocalDateTime createdAt,
            String message
    ) {
        this.transactionId = transactionId;
        this.transactionReference = transactionReference;
        this.senderUserId = senderUserId;
        this.receiverUserId = receiverUserId;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
        this.message = message;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public Long getSenderUserId() {
        return senderUserId;
    }

    public Long getReceiverUserId() {
        return receiverUserId;
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