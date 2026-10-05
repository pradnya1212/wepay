package com.wepay.backend.risk.service;

import com.wepay.backend.risk.dto.RiskAssessment;
import com.wepay.backend.risk.enums.RiskLevel;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class RiskEngineService {

    private static final BigDecimal MEDIUM_AMOUNT =
            new BigDecimal("25000");

    private static final BigDecimal HIGH_AMOUNT =
            new BigDecimal("50000");

    public RiskAssessment assess(
            Long senderUserId,
            Long receiverUserId,
            BigDecimal amount
    ) {

        int riskScore = 0;
        String reason = "Normal transaction";

        /*
         * Rule 1:
         * High-value transaction
         */
        if (amount.compareTo(HIGH_AMOUNT) > 0) {

            riskScore += 80;

            reason =
                    "High-value transaction detected";

        } else if (amount.compareTo(MEDIUM_AMOUNT) > 0) {

            riskScore += 40;

            reason =
                    "Medium-value transaction detected";
        }

        /*
         * Rule 2:
         * Invalid user IDs should never normally happen,
         * but we treat them as suspicious.
         */
        if (senderUserId == null || receiverUserId == null) {

            riskScore += 100;

            reason =
                    "Invalid transaction participants";
        }

        /*
         * Convert score into risk level.
         */
        RiskLevel riskLevel;

        if (riskScore >= 70) {

            riskLevel = RiskLevel.HIGH;

        } else if (riskScore >= 30) {

            riskLevel = RiskLevel.MEDIUM;

        } else {

            riskLevel = RiskLevel.LOW;
        }

        return new RiskAssessment(
                riskLevel,
                riskScore,
                reason
        );
    }
}