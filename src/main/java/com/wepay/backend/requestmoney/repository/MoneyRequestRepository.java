package com.wepay.backend.requestmoney.repository;

import com.wepay.backend.requestmoney.entity.MoneyRequest;
import com.wepay.backend.requestmoney.enums.MoneyRequestStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MoneyRequestRepository
        extends JpaRepository<MoneyRequest, Long> {

    List<MoneyRequest>
    findByRequesterUserIdOrderByCreatedAtDesc(
            Long requesterUserId
    );

    List<MoneyRequest>
    findByReceiverUserIdOrderByCreatedAtDesc(
            Long receiverUserId
    );

    Optional<MoneyRequest>
    findByIdAndRequesterUserId(
            Long id,
            Long requesterUserId
    );

    Optional<MoneyRequest>
    findByIdAndReceiverUserId(
            Long id,
            Long receiverUserId
    );

    boolean existsByRequesterUserIdAndReceiverUserIdAndStatus(
            Long requesterUserId,
            Long receiverUserId,
            MoneyRequestStatus status
    );
}