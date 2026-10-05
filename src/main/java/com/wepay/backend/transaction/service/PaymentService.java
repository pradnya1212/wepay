package com.wepay.backend.transaction.service;

import com.wepay.backend.bank.entity.BankAccount;
import com.wepay.backend.bank.repository.BankAccountRepository;
import com.wepay.backend.notification.enums.NotificationType;
import com.wepay.backend.notification.service.NotificationService;
import com.wepay.backend.pin.service.PaymentPinService;
import com.wepay.backend.risk.dto.RiskAssessment;
import com.wepay.backend.risk.enums.RiskLevel;
import com.wepay.backend.risk.service.RiskAuditService;
import com.wepay.backend.risk.service.RiskEngineService;
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

    private final RiskEngineService riskEngineService;
    private final RiskAuditService riskAuditService;

    public PaymentService(
            TransactionRepository transactionRepository,
            UserRepository userRepository,
            BankAccountRepository bankAccountRepository,
            PaymentProvider paymentProvider,
            PaymentPinService paymentPinService,
            NotificationService notificationService,
            WalletService walletService,
            RiskEngineService riskEngineService,
            RiskAuditService riskAuditService
    ) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.paymentProvider = paymentProvider;
        this.paymentPinService = paymentPinService;
        this.notificationService = notificationService;
        this.walletService = walletService;

        this.riskEngineService = riskEngineService;
        this.riskAuditService = riskAuditService;
    }

    @Transactional
    public Transaction sendMoney(
            Long senderUserId,
            PaymentRequest request
    ) {

        /*
         * =========================================================
         * 1. IDEMPOTENCY CHECK
         * =========================================================
         *
         * Prevent the same payment from being processed twice.
         */
        Optional<Transaction> existingTransaction =
                transactionRepository.findByIdempotencyKey(
                        request.getIdempotencyKey()
                );

        if (existingTransaction.isPresent()) {
            return existingTransaction.get();
        }

        /*
         * =========================================================
         * 2. SELF PAYMENT CHECK
         * =========================================================
         */
        if (senderUserId.equals(
                request.getReceiverUserId()
        )) {

            throw new RuntimeException(
                    "Sender and receiver cannot be the same"
            );
        }

        /*
         * =========================================================
         * 3. RECEIVER VALIDATION
         * =========================================================
         */
        if (!userRepository.existsById(
                request.getReceiverUserId()
        )) {

            throw new RuntimeException(
                    "Receiver user not found"
            );
        }

        /*
         * =========================================================
         * 4. FRAUD / RISK ASSESSMENT
         * =========================================================
         */
        RiskAssessment riskAssessment =
                riskEngineService.assess(
                        senderUserId,
                        request.getReceiverUserId(),
                        request.getAmount()
                );

        /*
         * =========================================================
         * 5. HIGH RISK
         * =========================================================
         *
         * HIGH risk transactions are blocked.
         */
        if (riskAssessment.getRiskLevel()
                == RiskLevel.HIGH) {

            riskAuditService.recordAssessment(
                    senderUserId,
                    request.getReceiverUserId(),
                    request.getAmount(),
                    riskAssessment,
                    "BLOCKED"
            );

            notificationService.createNotification(
                    senderUserId,
                    NotificationType.PAYMENT_FAILED,
                    "Payment blocked by fraud risk engine. "
                            + riskAssessment.getReason()
            );

            throw new RuntimeException(
                    "Transaction blocked by fraud risk engine. "
                            + riskAssessment.getReason()
            );
        }

        /*
         * =========================================================
         * 6. MEDIUM RISK
         * =========================================================
         *
         * Medium-risk payments are currently allowed,
         * but we record them as FLAGGED.
         */
        if (riskAssessment.getRiskLevel()
                == RiskLevel.MEDIUM) {

            riskAuditService.recordAssessment(
                    senderUserId,
                    request.getReceiverUserId(),
                    request.getAmount(),
                    riskAssessment,
                    "FLAGGED"
            );

        } else {

            /*
             * LOW risk transaction.
             */
            riskAuditService.recordAssessment(
                    senderUserId,
                    request.getReceiverUserId(),
                    request.getAmount(),
                    riskAssessment,
                    "ALLOWED"
            );
        }

        /*
         * =========================================================
         * 7. BANK ACCOUNT VALIDATION
         * =========================================================
         */
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

        /*
         * Bank account must be verified.
         */
        if (!bankAccount.isVerified()) {

            throw new RuntimeException(
                    "Bank account is not verified"
            );
        }

        /*
         * =========================================================
         * 8. PAYMENT PIN VALIDATION
         * =========================================================
         */
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

        /*
         * =========================================================
         * 9. PAYMENT PROVIDER
         * =========================================================
         */
        TransactionStatus status =
                paymentProvider.processPayment(
                        senderUserId,
                        request.getReceiverUserId(),
                        request.getAmount()
                );

        /*
         * =========================================================
         * 10. CREATE TRANSACTION
         * =========================================================
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
         * =========================================================
         * 11. SUCCESSFUL PAYMENT
         * =========================================================
         */
        if (status == TransactionStatus.SUCCESS) {

            /*
             * Debit sender wallet.
             */
            walletService.debit(
                    senderUserId,
                    request.getAmount(),
                    savedTransaction.getTransactionReference()
            );

            /*
             * Credit receiver wallet.
             */
            walletService.credit(
                    request.getReceiverUserId(),
                    request.getAmount(),
                    savedTransaction.getTransactionReference()
            );

            /*
             * Sender notification.
             */
            notificationService.createNotification(
                    senderUserId,
                    NotificationType.PAYMENT_SUCCESS,
                    "Payment of ₹"
                            + request.getAmount()
                            + " sent successfully."
            );

            /*
             * Receiver notification.
             */
            notificationService.createNotification(
                    request.getReceiverUserId(),
                    NotificationType.PAYMENT_SUCCESS,
                    "You received ₹"
                            + request.getAmount()
                            + " successfully."
            );

        } else if (status == TransactionStatus.FAILED) {

            /*
             * =====================================================
             * FAILED PAYMENT
             * =====================================================
             */
            notificationService.createNotification(
                    senderUserId,
                    NotificationType.PAYMENT_FAILED,
                    "Payment of ₹"
                            + request.getAmount()
                            + " failed."
            );
        }

        /*
         * Return saved transaction.
         */
        return savedTransaction;
    }
}