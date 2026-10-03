package com.wepay.backend.wallet.controller;

import com.wepay.backend.wallet.dto.WalletResponse;
import com.wepay.backend.wallet.dto.WalletTopUpRequest;
import com.wepay.backend.wallet.service.WalletService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    private final WalletService walletService;

    public WalletController(
            WalletService walletService
    ) {
        this.walletService = walletService;
    }

    @GetMapping("/balance")
    public ResponseEntity<WalletResponse> getBalance(
            Authentication authentication
    ) {

        Long userId =
                (Long) authentication.getPrincipal();

        return ResponseEntity.ok(
                walletService.getBalance(userId)
        );
    }

    @PostMapping("/top-up")
    public ResponseEntity<WalletResponse> topUp(
            Authentication authentication,
            @Valid @RequestBody WalletTopUpRequest request
    ) {

        Long userId =
                (Long) authentication.getPrincipal();

        return ResponseEntity.ok(
                walletService.topUp(
                        userId,
                        request
                )
        );
    }
}