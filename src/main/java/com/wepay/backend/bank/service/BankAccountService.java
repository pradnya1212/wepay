package com.wepay.backend.bank.service;

import com.wepay.backend.bank.dto.LinkBankAccountRequest;
import com.wepay.backend.bank.entity.BankAccount;
import com.wepay.backend.bank.provider.BankProvider;
import com.wepay.backend.bank.repository.BankAccountRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final BankProvider bankProvider;

    public BankAccountService(
            BankAccountRepository bankAccountRepository,
            BankProvider bankProvider
    ) {
        this.bankAccountRepository = bankAccountRepository;
        this.bankProvider = bankProvider;
    }

    public BankAccount linkBankAccount(
            Long userId,
            LinkBankAccountRequest request
    ) {

        BankAccount account = new BankAccount(
                userId,
                request.getBankName(),
                request.getAccountNumber(),
                request.getAccountHolderName()
        );

        boolean verified =
                bankProvider.verifyAccount(account);

        if (!verified) {
            throw new RuntimeException(
                    "Bank account verification failed"
            );
        }

        account.setVerified(true);

        List<BankAccount> existingAccounts =
                bankAccountRepository.findByUserId(userId);

        if (existingAccounts.isEmpty()) {
            account.setPrimaryAccount(true);
        }

        return bankAccountRepository.save(account);
    }

    public List<BankAccount> getUserBankAccounts(
            Long userId
    ) {

        return bankAccountRepository
                .findByUserId(userId);
    }

    public void deleteBankAccount(
            Long userId,
            Long accountId
    ) {

        BankAccount account =
                bankAccountRepository
                        .findByIdAndUserId(
                                accountId,
                                userId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Bank account not found"
                                )
                        );

        bankAccountRepository.delete(account);
    }
}