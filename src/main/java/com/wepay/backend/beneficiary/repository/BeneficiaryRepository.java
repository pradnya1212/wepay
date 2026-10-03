package com.wepay.backend.beneficiary.repository;

import com.wepay.backend.beneficiary.entity.Beneficiary;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BeneficiaryRepository
        extends JpaRepository<Beneficiary, Long> {

    List<Beneficiary> findByUserIdOrderByCreatedAtDesc(
            Long userId
    );

    Optional<Beneficiary> findByIdAndUserId(
            Long id,
            Long userId
    );

    Optional<Beneficiary> findByUserIdAndReceiverUserId(
            Long userId,
            Long receiverUserId
    );
}