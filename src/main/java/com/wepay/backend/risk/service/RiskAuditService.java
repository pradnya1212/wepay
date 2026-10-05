package com.wepay.backend.risk.service;

import com.wepay.backend.risk.dto.RiskAssessment;
import com.wepay.backend.risk.dto.RiskAuditResponse;
import com.wepay.backend.risk.entity.RiskAuditLog;
import com.wepay.backend.risk.enums.RiskLevel;
import com.wepay.backend.risk.repository.RiskAuditLogRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class RiskAuditService {

    private final RiskAuditLogRepository riskAuditLogRepository;

    public RiskAuditService(
            RiskAuditLogRepository riskAuditLogRepository
    ) {
        this.riskAuditLogRepository = riskAuditLogRepository;
    }

    @Transactional
    public RiskAuditResponse recordAssessment(
            Long senderUserId,
            Long receiverUserId,
            BigDecimal amount,
            RiskAssessment assessment,
            String decision
    ) {

        RiskAuditLog log =
                new RiskAuditLog(
                        senderUserId,
                        receiverUserId,
                        amount,
                        assessment.getRiskScore(),
                        assessment.getRiskLevel(),
                        assessment.getReason(),
                        decision
                );

        RiskAuditLog saved =
                riskAuditLogRepository.save(log);

        return new RiskAuditResponse(saved);
    }

    public List<RiskAuditResponse> getSenderHistory(
            Long senderUserId
    ) {

        List<RiskAuditLog> logs =
                riskAuditLogRepository
                        .findBySenderUserIdOrderByCreatedAtDesc(
                                senderUserId
                        );

        return logs.stream()
                .map(RiskAuditResponse::new)
                .toList();
    }

    public List<RiskAuditResponse> getReceiverHistory(
            Long receiverUserId
    ) {

        List<RiskAuditLog> logs =
                riskAuditLogRepository
                        .findByReceiverUserIdOrderByCreatedAtDesc(
                                receiverUserId
                        );

        return logs.stream()
                .map(RiskAuditResponse::new)
                .toList();
    }

    public List<RiskAuditResponse> getHighRiskTransactions() {

        List<RiskAuditLog> logs =
                riskAuditLogRepository
                        .findByRiskLevelOrderByCreatedAtDesc(
                                RiskLevel.HIGH
                        );

        return logs.stream()
                .map(RiskAuditResponse::new)
                .toList();
    }

    public List<RiskAuditResponse> getRecentTransactions() {

        List<RiskAuditLog> logs =
                riskAuditLogRepository
                        .findTop20ByOrderByCreatedAtDesc();

        return logs.stream()
                .map(RiskAuditResponse::new)
                .toList();
    }
}