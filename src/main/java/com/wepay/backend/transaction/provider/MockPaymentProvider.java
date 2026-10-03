package com.wepay.backend.transaction.provider;

import com.wepay.backend.transaction.enums.TransactionStatus;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class MockPaymentProvider implements PaymentProvider {

    private static final BigDecimal MAX_SIMULATED_BALANCE =
            new BigDecimal("10000.00");

    @Override
    public TransactionStatus processPayment(
            Long senderUserId,
            Long receiverUserId,
            BigDecimal amount
    ) {

        if (amount == null ||
                amount.compareTo(BigDecimal.ZERO) <= 0) {

            return TransactionStatus.FAILED;
        }

        if (amount.compareTo(
                MAX_SIMULATED_BALANCE
        ) > 0) {

            return TransactionStatus.FAILED;
        }

        return TransactionStatus.SUCCESS;
    }
}