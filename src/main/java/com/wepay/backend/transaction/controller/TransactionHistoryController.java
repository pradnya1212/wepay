package com.wepay.backend.transaction.controller;

import com.wepay.backend.transaction.dto.TransactionHistoryResponse;
import com.wepay.backend.transaction.entity.Transaction;
import com.wepay.backend.transaction.service.TransactionHistoryService;
import com.wepay.backend.user.entity.User;
import com.wepay.backend.user.repository.UserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionHistoryController {

    private final TransactionHistoryService transactionHistoryService;
    private final UserRepository userRepository;

    public TransactionHistoryController(
            TransactionHistoryService transactionHistoryService,
            UserRepository userRepository
    ) {
        this.transactionHistoryService = transactionHistoryService;
        this.userRepository = userRepository;
    }

    @GetMapping("/history")
    public ResponseEntity<List<TransactionHistoryResponse>>
    getTransactionHistory(
            Authentication authentication
    ) {

        Long userId =
                (Long) authentication.getPrincipal();

        List<TransactionHistoryResponse> transactions =
                transactionHistoryService
                        .getMyTransactions(userId);

        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionHistoryResponse>
    getTransactionById(
            Authentication authentication,
            @PathVariable Long id
    ) {

        Long userId =
                (Long) authentication.getPrincipal();

        Transaction transaction =
                transactionHistoryService
                        .getTransactionById(
                                id,
                                userId
                        );

        User sender =
                userRepository.findById(
                        transaction.getSenderUserId()
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Sender user not found"
                        )
                );

        User receiver =
                userRepository.findById(
                        transaction.getReceiverUserId()
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Receiver user not found"
                        )
                );

        TransactionHistoryResponse response =
                new TransactionHistoryResponse(
                        transaction.getId(),
                        transaction.getTransactionReference(),

                        sender.getId(),
                        sender.getName(),
                        sender.getMobileNumber(),

                        receiver.getId(),
                        receiver.getName(),
                        receiver.getMobileNumber(),

                        transaction.getAmount(),
                        transaction.getStatus().name(),
                        transaction.getCreatedAt()
                );

        return ResponseEntity.ok(response);
    }
}