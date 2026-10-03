package com.wepay.backend.requestmoney.controller;

import com.wepay.backend.requestmoney.dto.AcceptMoneyRequest;
import com.wepay.backend.requestmoney.dto.CreateMoneyRequest;
import com.wepay.backend.requestmoney.dto.MoneyRequestResponse;
import com.wepay.backend.requestmoney.service.MoneyRequestService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/money-requests")
public class MoneyRequestController {

    private final MoneyRequestService moneyRequestService;

    public MoneyRequestController(
            MoneyRequestService moneyRequestService
    ) {
        this.moneyRequestService =
                moneyRequestService;
    }

    /*
     * Create money request.
     */
    @PostMapping
    public ResponseEntity<MoneyRequestResponse> createRequest(
            Authentication authentication,
            @Valid @RequestBody CreateMoneyRequest request
    ) {

        Long userId =
                (Long) authentication.getPrincipal();

        return ResponseEntity.ok(
                moneyRequestService.createRequest(
                        userId,
                        request
                )
        );
    }

    /*
     * Get requests created by current user.
     */
    @GetMapping("/sent")
    public ResponseEntity<List<MoneyRequestResponse>> getSentRequests(
            Authentication authentication
    ) {

        Long userId =
                (Long) authentication.getPrincipal();

        return ResponseEntity.ok(
                moneyRequestService.getSentRequests(
                        userId
                )
        );
    }

    /*
     * Get requests received by current user.
     */
    @GetMapping("/received")
    public ResponseEntity<List<MoneyRequestResponse>> getReceivedRequests(
            Authentication authentication
    ) {

        Long userId =
                (Long) authentication.getPrincipal();

        return ResponseEntity.ok(
                moneyRequestService.getReceivedRequests(
                        userId
                )
        );
    }

    /*
     * Accept request.
     */
    @PostMapping("/{requestId}/accept")
    public ResponseEntity<MoneyRequestResponse> acceptRequest(
            Authentication authentication,
            @PathVariable Long requestId,
            @Valid @RequestBody AcceptMoneyRequest request
    ) {

        Long userId =
                (Long) authentication.getPrincipal();

        return ResponseEntity.ok(
                moneyRequestService.acceptRequest(
                        userId,
                        requestId,
                        request
                )
        );
    }

    /*
     * Decline request.
     */
    @PostMapping("/{requestId}/decline")
    public ResponseEntity<MoneyRequestResponse> declineRequest(
            Authentication authentication,
            @PathVariable Long requestId
    ) {

        Long userId =
                (Long) authentication.getPrincipal();

        return ResponseEntity.ok(
                moneyRequestService.declineRequest(
                        userId,
                        requestId
                )
        );
    }
}