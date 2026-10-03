package com.wepay.backend.transaction.service;

import com.wepay.backend.bank.entity.BankAccount;
import com.wepay.backend.bank.repository.BankAccountRepository;
import com.wepay.backend.notification.enums.NotificationType;
import com.wepay.backend.notification.service.NotificationService;
import com.wepay.backend.pin.service.PaymentPinService;
import com.wepay.backend.transaction.dto.PaymentRequest;
import com.wepay.backend.transaction.entity.Transaction;
import com.wepay.backend.transaction.enums.TransactionStatus;
import com.wepay.backend.transaction.provider.PaymentProvider;
import com.wepay.backend.transaction.repository.TransactionRepository;
import com.wepay.backend.user.repository.UserRepository;
import com.wepay.backend.wallet.service.WalletService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PaymentService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final BankAccountRepository bankAccountRepository;
    private final PaymentProvider paymentProvider;
    private final PaymentPinService paymentPinService;
    private final NotificationService notificationService;
    private final WalletService walletService;

    public PaymentService(
            TransactionRepository transactionRepository,
            UserRepository userRepository,
            BankAccountRepository bankAccountRepository,
            PaymentProvider paymentProvider,
            PaymentPinService paymentPinService,
            NotificationService notificationService,
            WalletService walletService
    ) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.paymentProvider = paymentProvider;
        this.paymentPinService = paymentPinService;
        this.notificationService = notificationService;
        this.walletService = walletService;
    }

    @Transactional
    public Transaction sendMoney(
            Long senderUserId,
            PaymentRequest request
    ) {

        Optional<Transaction> existingTransaction =
                transactionRepository.findByIdempotencyKey(
                        request.getIdempotencyKey()
                );

        if (existingTransaction.isPresent()) {
            return existingTransaction.get();
        }

        if (senderUserId.equals(
                request.getReceiverUserId()
        )) {

            throw new RuntimeException(
                    "Sender and receiver cannot be the same"
            );
        }

        if (!userRepository.existsById(
                request.getReceiverUserId()
        )) {

            throw new RuntimeException(
                    "Receiver user not found"
            );
        }

        BankAccount bankAccount =
                bankAccountRepository
                        .findByIdAndUserId(
                                request.getBankAccountId(),
                                senderUserId
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

        boolean pinValid =
                paymentPinService.verifyPin(
                        senderUserId,
                        request.getPin()
                );

        if (!pinValid) {

            throw new RuntimeException(
                    "Invalid payment PIN"
            );
        }

        TransactionStatus status =
                paymentProvider.processPayment(
                        senderUserId,
                        request.getReceiverUserId(),
                        request.getAmount()
                );

        /*
         * Create and save the payment transaction first.
         * This gives us the transaction ID.
         */
        Transaction transaction =
                new Transaction(
                        senderUserId,
                        request.getReceiverUserId(),
                        request.getAmount(),
                        status,
                        request.getIdempotencyKey()
                );

        Transaction savedTransaction =
                transactionRepository.save(transaction);

        /*
         * Only successful payments affect wallets.
         */
        if (status == TransactionStatus.SUCCESS) {

            walletService.debit(
                    senderUserId,
                    request.getAmount(),
                    savedTransaction.getTransactionReference()
            );

            walletService.credit(
                    request.getReceiverUserId(),
                    request.getAmount(),
                    savedTransaction.getTransactionReference()
            );
            notificationService.createNotification(
                    senderUserId,
                    NotificationType.PAYMENT_SUCCESS,
                    "Payment of ₹"
                            + request.getAmount()
                            + " sent successfully."
            );

            notificationService.createNotification(
                    request.getReceiverUserId(),
                    NotificationType.PAYMENT_SUCCESS,
                    "You received ₹"
                            + request.getAmount()
                            + " successfully."
            );

        } else if (status == TransactionStatus.FAILED) {

            notificationService.createNotification(
                    senderUserId,
                    NotificationType.PAYMENT_FAILED,
                    "Payment of ₹"
                            + request.getAmount()
                            + " failed."
            );
        }

        return savedTransaction;
    }
}