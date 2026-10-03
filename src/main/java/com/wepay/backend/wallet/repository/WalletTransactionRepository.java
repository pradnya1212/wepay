package com.wepay.backend.wallet.repository;

import com.wepay.backend.wallet.entity.WalletTransaction;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalletTransactionRepository
        extends JpaRepository<WalletTransaction, Long> {

    List<WalletTransaction>
    findByUserIdOrderByCreatedAtDesc(Long userId);
}