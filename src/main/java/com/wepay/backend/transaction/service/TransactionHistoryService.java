package com.wepay.backend.transaction.service;

import com.wepay.backend.transaction.dto.TransactionHistoryResponse;
import com.wepay.backend.transaction.entity.Transaction;
import com.wepay.backend.transaction.repository.TransactionRepository;
import com.wepay.backend.user.entity.User;
import com.wepay.backend.user.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionHistoryService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionHistoryService(
            TransactionRepository transactionRepository,
            UserRepository userRepository
    ) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    public List<TransactionHistoryResponse> getMyTransactions(
            Long userId
    ) {

        List<Transaction> transactions =
                transactionRepository
                        .findBySenderUserIdOrReceiverUserId(
                                userId,
                                userId
                        );

        return transactions.stream()
                .map(this::convertToResponse)
                .toList();
    }

    private TransactionHistoryResponse convertToResponse(
            Transaction transaction
    ) {

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

        return new TransactionHistoryResponse(
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
    }

    public Transaction getTransactionById(
            Long transactionId,
            Long userId
    ) {

        Optional<Transaction> sentTransaction =
                transactionRepository
                        .findByIdAndSenderUserId(
                                transactionId,
                                userId
                        );

        if (sentTransaction.isPresent()) {
            return sentTransaction.get();
        }

        Optional<Transaction> receivedTransaction =
                transactionRepository
                        .findByIdAndReceiverUserId(
                                transactionId,
                                userId
                        );

        if (receivedTransaction.isPresent()) {
            return receivedTransaction.get();
        }

        throw new RuntimeException(
                "Transaction not found"
        );
    }
}