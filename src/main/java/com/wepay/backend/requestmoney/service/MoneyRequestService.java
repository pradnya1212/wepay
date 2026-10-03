package com.wepay.backend.requestmoney.service;

import com.wepay.backend.requestmoney.dto.AcceptMoneyRequest;
import com.wepay.backend.requestmoney.dto.CreateMoneyRequest;
import com.wepay.backend.requestmoney.dto.MoneyRequestResponse;
import com.wepay.backend.requestmoney.entity.MoneyRequest;
import com.wepay.backend.requestmoney.enums.MoneyRequestStatus;
import com.wepay.backend.requestmoney.repository.MoneyRequestRepository;

import com.wepay.backend.transaction.dto.PaymentRequest;
import com.wepay.backend.transaction.entity.Transaction;
import com.wepay.backend.transaction.enums.TransactionStatus;
import com.wepay.backend.transaction.service.PaymentService;

import com.wepay.backend.user.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MoneyRequestService {

    private static final long REQUEST_EXPIRY_HOURS = 24L;

    private final MoneyRequestRepository moneyRequestRepository;
    private final UserRepository userRepository;
    private final PaymentService paymentService;

    public MoneyRequestService(
            MoneyRequestRepository moneyRequestRepository,
            UserRepository userRepository,
            PaymentService paymentService
    ) {
        this.moneyRequestRepository = moneyRequestRepository;
        this.userRepository = userRepository;
        this.paymentService = paymentService;
    }

    /*
     * Create a money request.
     */
    @Transactional
    public MoneyRequestResponse createRequest(
            Long requesterUserId,
            CreateMoneyRequest request
    ) {

        Long receiverUserId = request.getReceiverUserId();

        // Receiver must exist
        if (!userRepository.existsById(receiverUserId)) {
            throw new RuntimeException("Receiver user not found");
        }

        // User cannot request money from himself
        if (requesterUserId.equals(receiverUserId)) {
            throw new RuntimeException(
                    "You cannot request money from yourself"
            );
        }

        // Prevent duplicate pending request
        boolean alreadyExists =
                moneyRequestRepository
                        .existsByRequesterUserIdAndReceiverUserIdAndStatus(
                                requesterUserId,
                                receiverUserId,
                                MoneyRequestStatus.PENDING
                        );

        if (alreadyExists) {
            throw new RuntimeException(
                    "A pending money request already exists"
            );
        }

        LocalDateTime expiresAt =
                LocalDateTime.now()
                        .plusHours(REQUEST_EXPIRY_HOURS);

        MoneyRequest moneyRequest =
                new MoneyRequest(
                        requesterUserId,
                        receiverUserId,
                        request.getAmount(),
                        request.getNote(),
                        expiresAt
                );

        MoneyRequest saved =
                moneyRequestRepository.save(moneyRequest);

        return new MoneyRequestResponse(saved);
    }

    /*
     * Requests created by current user.
     */
    @Transactional
    public List<MoneyRequestResponse> getSentRequests(
            Long userId
    ) {

        updateExpiredRequests(userId, true);

        return moneyRequestRepository
                .findByRequesterUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(MoneyRequestResponse::new)
                .toList();
    }

    /*
     * Requests received by current user.
     */
    @Transactional
    public List<MoneyRequestResponse> getReceivedRequests(
            Long userId
    ) {

        updateExpiredRequests(userId, false);

        return moneyRequestRepository
                .findByReceiverUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(MoneyRequestResponse::new)
                .toList();
    }

    /*
     * Accept a money request.
     *
     * The receiver of the money request becomes
     * the sender of the actual payment.
     */
    @Transactional
    public MoneyRequestResponse acceptRequest(
            Long receiverUserId,
            Long requestId,
            AcceptMoneyRequest request
    ) {

        MoneyRequest moneyRequest =
                moneyRequestRepository
                        .findByIdAndReceiverUserId(
                                requestId,
                                receiverUserId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Money request not found"
                                )
                        );

        // Only PENDING request can be accepted
        if (moneyRequest.getStatus()
                != MoneyRequestStatus.PENDING) {

            throw new RuntimeException(
                    "Money request is no longer pending"
            );
        }

        // Check expiry
        if (isExpired(moneyRequest)) {

            moneyRequest.setStatus(
                    MoneyRequestStatus.EXPIRED
            );

            moneyRequestRepository.save(moneyRequest);

            throw new RuntimeException(
                    "Money request has expired"
            );
        }

        /*
         * Existing PaymentService is reused.
         *
         * Money request:
         *
         * requesterUserId = person who requested money
         * receiverUserId  = person who needs to pay
         *
         * Therefore:
         *
         * receiverUserId -> sender
         * requesterUserId -> receiver
         */

        PaymentRequest paymentRequest =
                new PaymentRequest();

        paymentRequest.setReceiverUserId(
                moneyRequest.getRequesterUserId()
        );

        paymentRequest.setBankAccountId(
                request.getBankAccountId()
        );

        paymentRequest.setAmount(
                moneyRequest.getAmount()
        );

        paymentRequest.setPin(
                request.getPin()
        );

        paymentRequest.setIdempotencyKey(
                request.getIdempotencyKey()
        );

        /*
         * Call existing PaymentService.
         *
         * It returns Transaction.
         */
        Transaction transaction =
                paymentService.sendMoney(
                        receiverUserId,
                        paymentRequest
                );

        /*
         * Only successful payment should
         * change request status to ACCEPTED.
         */
        if (transaction.getStatus()
                != TransactionStatus.SUCCESS) {

            throw new RuntimeException(
                    "Payment failed. Money request remains pending."
            );
        }

        moneyRequest.setStatus(
                MoneyRequestStatus.ACCEPTED
        );

        MoneyRequest saved =
                moneyRequestRepository.save(moneyRequest);

        return new MoneyRequestResponse(saved);
    }

    /*
     * Decline a money request.
     */
    @Transactional
    public MoneyRequestResponse declineRequest(
            Long receiverUserId,
            Long requestId
    ) {

        MoneyRequest moneyRequest =
                moneyRequestRepository
                        .findByIdAndReceiverUserId(
                                requestId,
                                receiverUserId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Money request not found"
                                )
                        );

        if (moneyRequest.getStatus()
                != MoneyRequestStatus.PENDING) {

            throw new RuntimeException(
                    "Money request is no longer pending"
            );
        }

        if (isExpired(moneyRequest)) {

            moneyRequest.setStatus(
                    MoneyRequestStatus.EXPIRED
            );

            moneyRequestRepository.save(moneyRequest);

            throw new RuntimeException(
                    "Money request has expired"
            );
        }

        moneyRequest.setStatus(
                MoneyRequestStatus.DECLINED
        );

        MoneyRequest saved =
                moneyRequestRepository.save(moneyRequest);

        return new MoneyRequestResponse(saved);
    }

    /*
     * Update expired requests.
     */
    private void updateExpiredRequests(
            Long userId,
            boolean requester
    ) {

        List<MoneyRequest> requests;

        if (requester) {

            requests =
                    moneyRequestRepository
                            .findByRequesterUserIdOrderByCreatedAtDesc(
                                    userId
                            );

        } else {

            requests =
                    moneyRequestRepository
                            .findByReceiverUserIdOrderByCreatedAtDesc(
                                    userId
                            );
        }

        LocalDateTime now =
                LocalDateTime.now();

        for (MoneyRequest request : requests) {

            if (request.getStatus()
                    == MoneyRequestStatus.PENDING
                    &&
                    now.isAfter(
                            request.getExpiresAt()
                    )) {

                request.setStatus(
                        MoneyRequestStatus.EXPIRED
                );

                moneyRequestRepository.save(request);
            }
        }
    }

    /*
     * Check whether request is expired.
     */
    private boolean isExpired(
            MoneyRequest moneyRequest
    ) {

        return LocalDateTime.now()
                .isAfter(
                        moneyRequest.getExpiresAt()
                );
    }
}