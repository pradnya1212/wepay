package com.wepay.backend.transaction.repository;

import com.wepay.backend.transaction.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefundRepository
        extends JpaRepository<Refund, Long> {

    List<Refund> findByRequestedByUserId(
            Long userId
    );

    Optional<Refund> findByTransactionId(
            Long transactionId
    );
}