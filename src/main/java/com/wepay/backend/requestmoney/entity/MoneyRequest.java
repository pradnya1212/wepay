package com.wepay.backend.requestmoney.entity;

import com.wepay.backend.requestmoney.enums.MoneyRequestStatus;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "money_requests",
        indexes = {
                @Index(name = "idx_money_request_requester",
                        columnList = "requester_user_id"),
                @Index(name = "idx_money_request_receiver",
                        columnList = "receiver_user_id")
        }
)
public class MoneyRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "requester_user_id", nullable = false)
    private Long requesterUserId;

    @Column(name = "receiver_user_id", nullable = false)
    private Long receiverUserId;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 200)
    private String note;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MoneyRequestStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    public MoneyRequest() {
    }

    public MoneyRequest(
            Long requesterUserId,
            Long receiverUserId,
            BigDecimal amount,
            String note,
            LocalDateTime expiresAt
    ) {
        this.requesterUserId = requesterUserId;
        this.receiverUserId = receiverUserId;
        this.amount = amount;
        this.note = note;
        this.status = MoneyRequestStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = expiresAt;
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

    public void setStatus(MoneyRequestStatus status) {
        this.status = status;
    }
}