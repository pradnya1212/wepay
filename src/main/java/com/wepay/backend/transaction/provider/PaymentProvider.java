package com.wepay.backend.transaction.provider;

import com.wepay.backend.transaction.enums.TransactionStatus;

import java.math.BigDecimal;

public interface PaymentProvider {

    TransactionStatus processPayment(
            Long senderUserId,
            Long receiverUserId,
            BigDecimal amount
    );
}