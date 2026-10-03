package com.wepay.backend.transaction.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionHistoryResponse {

    private Long transactionId;
    private String transactionReference;

    private Long senderUserId;
    private String senderName;
    private String senderMobileNumber;

    private Long receiverUserId;
    private String receiverName;
    private String receiverMobileNumber;

    private BigDecimal amount;

    private String status;

    private LocalDateTime createdAt;

    public TransactionHistoryResponse(
            Long transactionId,
            String transactionReference,
            Long senderUserId,
            String senderName,
            String senderMobileNumber,
            Long receiverUserId,
            String receiverName,
            String receiverMobileNumber,
            BigDecimal amount,
            String status,
            LocalDateTime createdAt
    ) {
        this.transactionId = transactionId;
        this.transactionReference = transactionReference;

        this.senderUserId = senderUserId;
        this.senderName = senderName;
        this.senderMobileNumber = senderMobileNumber;

        this.receiverUserId = receiverUserId;
        this.receiverName = receiverName;
        this.receiverMobileNumber = receiverMobileNumber;

        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
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

    public String getSenderName() {
        return senderName;
    }

    public String getSenderMobileNumber() {
        return senderMobileNumber;
    }

    public Long getReceiverUserId() {
        return receiverUserId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public String getReceiverMobileNumber() {
        return receiverMobileNumber;
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
}