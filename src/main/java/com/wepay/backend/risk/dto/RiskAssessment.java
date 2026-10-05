package com.wepay.backend.risk.dto;

import com.wepay.backend.risk.enums.RiskLevel;

public class RiskAssessment {

    private final RiskLevel riskLevel;
    private final int riskScore;
    private final String reason;

    public RiskAssessment(
            RiskLevel riskLevel,
            int riskScore,
            String reason
    ) {
        this.riskLevel = riskLevel;
        this.riskScore = riskScore;
        this.reason = reason;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public int getRiskScore() {
        return riskScore;
    }

    public String getReason() {
        return reason;
    }
}