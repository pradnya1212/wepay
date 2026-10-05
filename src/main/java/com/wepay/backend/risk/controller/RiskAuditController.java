package com.wepay.backend.risk.controller;

import com.wepay.backend.risk.dto.RiskAuditResponse;
import com.wepay.backend.risk.service.RiskAuditService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/risk")
public class RiskAuditController {

    private final RiskAuditService riskAuditService;

    public RiskAuditController(
            RiskAuditService riskAuditService
    ) {
        this.riskAuditService = riskAuditService;
    }

    /*
     * Get risk history for logged-in user as sender.
     */
    @GetMapping("/my-history")
    public ResponseEntity<List<RiskAuditResponse>> getMyHistory(
            Authentication authentication
    ) {

        Long userId =
                (Long) authentication.getPrincipal();

        return ResponseEntity.ok(
                riskAuditService.getSenderHistory(userId)
        );
    }

    /*
     * Get risk history where logged-in user
     * was the receiver.
     */
    @GetMapping("/received-history")
    public ResponseEntity<List<RiskAuditResponse>> getReceivedHistory(
            Authentication authentication
    ) {

        Long userId =
                (Long) authentication.getPrincipal();

        return ResponseEntity.ok(
                riskAuditService.getReceiverHistory(userId)
        );
    }

    /*
     * Get latest 20 risk assessments.
     *
     * Later this endpoint should be restricted
     * to ADMIN users.
     */
    @GetMapping("/recent")
    public ResponseEntity<List<RiskAuditResponse>> getRecent() {

        return ResponseEntity.ok(
                riskAuditService.getRecentTransactions()
        );
    }

    /*
     * Get all HIGH risk transactions.
     *
     * Later this endpoint should be restricted
     * to ADMIN users.
     */
    @GetMapping("/high-risk")
    public ResponseEntity<List<RiskAuditResponse>> getHighRisk() {

        return ResponseEntity.ok(
                riskAuditService.getHighRiskTransactions()
        );
    }
}