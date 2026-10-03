package com.wepay.backend.requestmoney.dto;

import com.wepay.backend.requestmoney.entity.MoneyRequest;
import com.wepay.backend.requestmoney.enums.MoneyRequestStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MoneyRequestResponse {

    private Long id;
    private Long requesterUserId;
    private Long receiverUserId;
    private BigDecimal amount;
    private String note;
    private MoneyRequestStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    public MoneyRequestResponse(
            MoneyRequest request
    ) {
        this.id = request.getId();
        this.requesterUserId =
                request.getRequesterUserId();
        this.receiverUserId =
                request.getReceiverUserId();
        this.amount =
                request.getAmount();
        this.note =
                request.getNote();
        this.status =
                request.getStatus();
        this.createdAt =
                request.getCreatedAt();
        this.expiresAt =
                request.getExpiresAt();
    }

    public Long getId() {
        return id;
    }

    public Long getRequesterUserId() {
        return requesterUserId;
    }

    public Long getReceiverUserId() {
        return receiverUserId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getNote() {
        return note;
    }

    public MoneyRequestStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
}