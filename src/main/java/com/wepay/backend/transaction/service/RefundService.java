package com.wepay.backend.transaction.service;

import com.wepay.backend.notification.enums.NotificationType;
import com.wepay.backend.notification.service.NotificationService;
import com.wepay.backend.pin.service.PaymentPinService;
import com.wepay.backend.transaction.dto.RefundRequest;
import com.wepay.backend.transaction.entity.Refund;
import com.wepay.backend.transaction.entity.Transaction;
import com.wepay.backend.transaction.enums.RefundStatus;
import com.wepay.backend.transaction.enums.TransactionStatus;
import com.wepay.backend.transaction.repository.RefundRepository;
import com.wepay.backend.transaction.repository.TransactionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RefundService {

    private final RefundRepository refundRepository;
    private final TransactionRepository transactionRepository;
    private final PaymentPinService paymentPinService;
    private final NotificationService notificationService;

    public RefundService(
            RefundRepository refundRepository,
            TransactionRepository transactionRepository,
            PaymentPinService paymentPinService,
            NotificationService notificationService
    ) {
        this.refundRepository = refundRepository;
        this.transactionRepository = transactionRepository;
        this.paymentPinService = paymentPinService;
        this.notificationService = notificationService;
    }

    @Transactional
    public Refund requestRefund(
            Long userId,
            Long transactionId,
            RefundRequest request
    ) {

        /*
         * STEP 1:
         * Find transaction owned by the requester.
         */
        Transaction transaction =
                transactionRepository
                        .findByIdAndSenderUserId(
                                transactionId,
                                userId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Transaction not found"
                                )
                        );

        /*
         * STEP 2:
         * Only SUCCESS payments can be refunded.
         */
        if (transaction.getStatus()
                != TransactionStatus.SUCCESS) {

            throw new RuntimeException(
                    "Only successful transactions can be refunded"
            );
        }

        /*
         * STEP 3:
         * Prevent duplicate refund.
         */
        if (refundRepository
                .findByTransactionId(transactionId)
                .isPresent()) {

            throw new RuntimeException(
                    "Refund already requested for this transaction"
            );
        }

        /*
         * STEP 4:
         * Verify payment PIN.
         */
        boolean pinValid =
                paymentPinService.verifyPin(
                        userId,
                        request.getPin()
                );

        if (!pinValid) {

            throw new RuntimeException(
                    "Invalid payment PIN"
            );
        }

        /*
         * STEP 5:
         * Create refund.
         */
        Refund refund =
                new Refund(
                        transaction.getId(),
                        userId,
                        transaction.getAmount(),
                        RefundStatus.SUCCESS
                );

        /*
         * STEP 6:
         * Save refund.
         */
        Refund savedRefund =
                refundRepository.save(refund);

        /*
         * STEP 7:
         * Create refund notification.
         */
        notificationService.createNotification(
                userId,
                NotificationType.REFUND_SUCCESS,
                "Refund of ₹"
                        + transaction.getAmount()
                        + " processed successfully."
        );

        return savedRefund;
    }
}