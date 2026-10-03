package com.wepay.backend.wallet.service;

import com.wepay.backend.wallet.dto.WalletResponse;
import com.wepay.backend.wallet.dto.WalletTopUpRequest;
import com.wepay.backend.wallet.entity.Wallet;
import com.wepay.backend.wallet.entity.WalletTransaction;
import com.wepay.backend.wallet.enums.WalletTransactionType;
import com.wepay.backend.wallet.repository.WalletRepository;
import com.wepay.backend.wallet.repository.WalletTransactionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;

    public WalletService(
            WalletRepository walletRepository,
            WalletTransactionRepository walletTransactionRepository
    ) {
        this.walletRepository = walletRepository;
        this.walletTransactionRepository =
                walletTransactionRepository;
    }

    private Wallet getOrCreateWallet(Long userId) {

        return walletRepository
                .findByUserId(userId)
                .orElseGet(() ->
                        walletRepository.save(
                                new Wallet(userId)
                        )
                );
    }

    public WalletResponse getBalance(Long userId) {

        Wallet wallet =
                getOrCreateWallet(userId);

        return new WalletResponse(
                userId,
                wallet.getBalance()
        );
    }

    @Transactional
    public WalletResponse topUp(
            Long userId,
            WalletTopUpRequest request
    ) {

        Wallet wallet =
                getOrCreateWallet(userId);

        BigDecimal newBalance =
                wallet.getBalance()
                        .add(request.getAmount());

        wallet.setBalance(newBalance);

        Wallet savedWallet =
                walletRepository.save(wallet);

        WalletTransaction transaction =
                new WalletTransaction(
                        userId,
                        WalletTransactionType.TOP_UP,
                        request.getAmount(),
                        savedWallet.getBalance(),
                        "WALLET",
                        String.valueOf(savedWallet.getId()),
                        "Wallet top-up"
                );

        walletTransactionRepository.save(transaction);

        return new WalletResponse(
                userId,
                savedWallet.getBalance()
        );
    }

    @Transactional
    public void debit(
            Long userId,
            BigDecimal amount,
            String paymentTransactionReference
    ) {

        Wallet wallet =
                getOrCreateWallet(userId);

        if (wallet.getBalance()
                .compareTo(amount) < 0) {

            throw new RuntimeException(
                    "Insufficient wallet balance"
            );
        }

        wallet.setBalance(
                wallet.getBalance()
                        .subtract(amount)
        );

        Wallet savedWallet =
                walletRepository.save(wallet);

        WalletTransaction transaction =
                new WalletTransaction(
                        userId,
                        WalletTransactionType.PAYMENT_DEBIT,
                        amount,
                        savedWallet.getBalance(),
                        "PAYMENT",
                        paymentTransactionReference,
                        "Payment amount debited"
                );

        walletTransactionRepository.save(transaction);
    }

    @Transactional
    public void credit(
            Long userId,
            BigDecimal amount,
            String paymentTransactionReference
    ) {

        Wallet wallet =
                getOrCreateWallet(userId);

        wallet.setBalance(
                wallet.getBalance()
                        .add(amount)
        );

        Wallet savedWallet =
                walletRepository.save(wallet);

        WalletTransaction transaction =
                new WalletTransaction(
                        userId,
                        WalletTransactionType.PAYMENT_CREDIT,
                        amount,
                        savedWallet.getBalance(),
                        "PAYMENT",
                        paymentTransactionReference,
                        "Payment amount received"
                );

        walletTransactionRepository.save(transaction);
    }
}