package com.wepay.backend.transaction.repository;

import com.wepay.backend.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {

    List<Transaction> findBySenderUserId(Long senderUserId);

    List<Transaction> findByReceiverUserId(Long receiverUserId);

    List<Transaction> findBySenderUserIdOrReceiverUserId(
            Long senderUserId,
            Long receiverUserId
    );

    Optional<Transaction> findByIdAndSenderUserId(
            Long id,
            Long senderUserId
    );

    Optional<Transaction> findByIdAndReceiverUserId(
            Long id,
            Long receiverUserId
    );

    Optional<Transaction> findByIdempotencyKey(
            String idempotencyKey
    );
}