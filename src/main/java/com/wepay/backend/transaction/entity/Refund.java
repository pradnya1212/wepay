package com.wepay.backend.transaction.entity;

import com.wepay.backend.transaction.enums.RefundStatus;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "refunds")
public class Refund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long transactionId;

    @Column(nullable = false)
    private Long requestedByUserId;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RefundStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public Refund() {
    }

    public Refund(
            Long transactionId,
            Long requestedByUserId,
            BigDecimal amount,
            RefundStatus status
    ) {
        this.transactionId = transactionId;
        this.requestedByUserId = requestedByUserId;
        this.amount = amount;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public Long getRequestedByUserId() {
        return requestedByUserId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public RefundStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setStatus(RefundStatus status) {
        this.status = status;
    }
}