package com.wepay.backend.upi.service;

import com.wepay.backend.bank.entity.BankAccount;
import com.wepay.backend.bank.repository.BankAccountRepository;
import com.wepay.backend.upi.dto.CreateUpiRequest;
import com.wepay.backend.upi.entity.UpiAccount;
import com.wepay.backend.upi.repository.UpiAccountRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UpiAccountService {

    private final UpiAccountRepository upiAccountRepository;
    private final BankAccountRepository bankAccountRepository;

    public UpiAccountService(
            UpiAccountRepository upiAccountRepository,
            BankAccountRepository bankAccountRepository
    ) {
        this.upiAccountRepository = upiAccountRepository;
        this.bankAccountRepository = bankAccountRepository;
    }

    public UpiAccount createUpiId(
            Long userId,
            CreateUpiRequest request
    ) {

        if (upiAccountRepository.existsByUpiId(
                request.getUpiId()
        )) {

            throw new RuntimeException(
                    "UPI ID already exists"
            );
        }

        BankAccount bankAccount =
                bankAccountRepository
                        .findByIdAndUserId(
                                request.getBankAccountId(),
                                userId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Bank account not found"
                                )
                        );

        if (!bankAccount.isVerified()) {

            throw new RuntimeException(
                    "Bank account is not verified"
            );
        }

        UpiAccount upiAccount =
                new UpiAccount(
                        userId,
                        request.getUpiId(),
                        bankAccount.getId()
                );

        return upiAccountRepository.save(
                upiAccount
        );
    }

    public List<UpiAccount> getMyUpiAccounts(
            Long userId
    ) {

        return upiAccountRepository
                .findByUserId(userId);
    }

    public void deactivateUpiId(
            Long userId,
            Long upiAccountId
    ) {

        UpiAccount upiAccount =
                upiAccountRepository
                        .findByIdAndUserId(
                                upiAccountId,
                                userId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "UPI account not found"
                                )
                        );

        upiAccount.setActive(false);

        upiAccountRepository.save(
                upiAccount
        );
    }
}