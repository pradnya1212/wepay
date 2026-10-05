package com.wepay.backend.risk.entity;

import com.wepay.backend.risk.enums.RiskLevel;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "risk_audit_logs",
        indexes = {
                @Index(name = "idx_risk_audit_sender",
                        columnList = "sender_user_id"),
                @Index(name = "idx_risk_audit_receiver",
                        columnList = "receiver_user_id"),
                @Index(name = "idx_risk_audit_level",
                        columnList = "risk_level")
        }
)
public class RiskAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sender_user_id", nullable = false)
    private Long senderUserId;

    @Column(name = "receiver_user_id", nullable = false)
    private Long receiverUserId;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private int riskScore;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RiskLevel riskLevel;

    @Column(nullable = false, length = 500)
    private String reason;

    @Column(nullable = false, length = 30)
    private String decision;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected RiskAuditLog() {
    }

    public RiskAuditLog(
            Long senderUserId,
            Long receiverUserId,
            BigDecimal amount,
            int riskScore,
            RiskLevel riskLevel,
            String reason,
            String decision
    ) {
        this.senderUserId = senderUserId;
        this.receiverUserId = receiverUserId;
        this.amount = amount;
        this.riskScore = riskScore;
        this.riskLevel = riskLevel;
        this.reason = reason;
        this.decision = decision;
        this.createdAt = LocalDateTime.now();
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