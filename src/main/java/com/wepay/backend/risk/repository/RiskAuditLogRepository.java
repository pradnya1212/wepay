package com.wepay.backend.risk.repository;

import com.wepay.backend.risk.entity.RiskAuditLog;
import com.wepay.backend.risk.enums.RiskLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RiskAuditLogRepository
        extends JpaRepository<RiskAuditLog, Long> {

    List<RiskAuditLog>
    findBySenderUserIdOrderByCreatedAtDesc(
            Long senderUserId
    );

    List<RiskAuditLog>
    findByReceiverUserIdOrderByCreatedAtDesc(
            Long receiverUserId
    );

    List<RiskAuditLog>
    findByRiskLevelOrderByCreatedAtDesc(
            RiskLevel riskLevel
    );

    List<RiskAuditLog>
    findTop20ByOrderByCreatedAtDesc();
}