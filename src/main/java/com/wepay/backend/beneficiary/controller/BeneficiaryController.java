package com.wepay.backend.beneficiary.controller;

import com.wepay.backend.beneficiary.dto.AddBeneficiaryRequest;
import com.wepay.backend.beneficiary.dto.BeneficiaryResponse;
import com.wepay.backend.beneficiary.service.BeneficiaryService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/beneficiaries")
public class BeneficiaryController {

    private final BeneficiaryService beneficiaryService;

    public BeneficiaryController(
            BeneficiaryService beneficiaryService
    ) {
        this.beneficiaryService =
                beneficiaryService;
    }

    /*
     * Add beneficiary
     */
    @PostMapping
    public ResponseEntity<BeneficiaryResponse> addBeneficiary(
            Authentication authentication,
            @Valid @RequestBody AddBeneficiaryRequest request
    ) {

        Long userId =
                (Long) authentication.getPrincipal();

        BeneficiaryResponse response =
                beneficiaryService.addBeneficiary(
                        userId,
                        request
                );

        return ResponseEntity.ok(response);
    }

    /*
     * Get current user's beneficiaries
     */
    @GetMapping
    public ResponseEntity<List<BeneficiaryResponse>> getBeneficiaries(
            Authentication authentication
    ) {

        Long userId =
                (Long) authentication.getPrincipal();

        return ResponseEntity.ok(
                beneficiaryService.getBeneficiaries(
                        userId
                )
        );
    }

    /*
     * Delete / deactivate beneficiary
     */
    @DeleteMapping("/{beneficiaryId}")
    public ResponseEntity<Map<String, String>> deleteBeneficiary(
            Authentication authentication,
            @PathVariable Long beneficiaryId
    ) {

        Long userId =
                (Long) authentication.getPrincipal();

        beneficiaryService.deleteBeneficiary(
                userId,
                beneficiaryId
        );

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Beneficiary deleted successfully"
                )
        );
    }
}