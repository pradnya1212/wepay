package com.wepay.backend.risk.dto;

import com.wepay.backend.risk.entity.RiskAuditLog;
import com.wepay.backend.risk.enums.RiskLevel;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RiskAuditResponse {

    private Long id;
    private Long senderUserId;
    private Long receiverUserId;
    private BigDecimal amount;
    private int riskScore;
    private RiskLevel riskLevel;
    private String reason;
    private String decision;
    private LocalDateTime createdAt;

    public RiskAuditResponse(RiskAuditLog log) {
        this.id = log.getId();
        this.senderUserId = log.getSenderUserId();
        this.receiverUserId = log.getReceiverUserId();
        this.amount = log.getAmount();
        this.riskScore = log.getRiskScore();
        this.riskLevel = log.getRiskLevel();
        this.reason = log.getReason();
        this.decision = log.getDecision();
        this.createdAt = log.getCreatedAt();
    }

    public Long getId() {
        return id;
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

    public int getRiskScore() {
        return riskScore;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public String getReason() {
        return reason;
    }

    public String getDecision() {
        return decision;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}