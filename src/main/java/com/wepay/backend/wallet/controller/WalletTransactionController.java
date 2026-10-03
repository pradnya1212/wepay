package com.wepay.backend.wallet.controller;

import com.wepay.backend.wallet.dto.WalletTransactionResponse;
import com.wepay.backend.wallet.entity.WalletTransaction;
import com.wepay.backend.wallet.repository.WalletTransactionRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wallet/transactions")
public class WalletTransactionController {

    private final WalletTransactionRepository walletTransactionRepository;

    public WalletTransactionController(
            WalletTransactionRepository walletTransactionRepository
    ) {
        this.walletTransactionRepository =
                walletTransactionRepository;
    }

    @GetMapping
    public ResponseEntity<List<WalletTransactionResponse>>
    getWalletTransactions(
            Authentication authentication
    ) {

        Long userId =
                (Long) authentication.getPrincipal();

        List<WalletTransaction> transactions =
                walletTransactionRepository
                        .findByUserIdOrderByCreatedAtDesc(userId);

        List<WalletTransactionResponse> response =
                transactions.stream()
                        .map(transaction ->
                                new WalletTransactionResponse(
                                        transaction.getId(),
                                        transaction.getType().name(),
                                        transaction.getAmount(),
                                        transaction.getBalanceAfter(),
                                        transaction.getReferenceType(),
                                        transaction.getReferenceId(),
                                        transaction.getDescription(),
                                        transaction.getCreatedAt()
                                )
                        )
                        .toList();

        return ResponseEntity.ok(response);
    }
}