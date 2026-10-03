package com.wepay.backend.beneficiary.service;

import com.wepay.backend.beneficiary.dto.AddBeneficiaryRequest;
import com.wepay.backend.beneficiary.dto.BeneficiaryResponse;
import com.wepay.backend.beneficiary.entity.Beneficiary;
import com.wepay.backend.beneficiary.repository.BeneficiaryRepository;
import com.wepay.backend.user.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BeneficiaryService {

    private final BeneficiaryRepository beneficiaryRepository;
    private final UserRepository userRepository;

    public BeneficiaryService(
            BeneficiaryRepository beneficiaryRepository,
            UserRepository userRepository
    ) {
        this.beneficiaryRepository = beneficiaryRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public BeneficiaryResponse addBeneficiary(
            Long userId,
            AddBeneficiaryRequest request
    ) {

        Long receiverUserId =
                request.getReceiverUserId();

        // Receiver must exist
        if (!userRepository.existsById(receiverUserId)) {

            throw new RuntimeException(
                    "Receiver user not found"
            );
        }

        // User cannot add himself
        if (userId.equals(receiverUserId)) {

            throw new RuntimeException(
                    "You cannot add yourself as a beneficiary"
            );
        }

        /*
         * Check whether this beneficiary already exists.
         */
        Beneficiary beneficiary =
                beneficiaryRepository
                        .findByUserIdAndReceiverUserId(
                                userId,
                                receiverUserId
                        )
                        .orElse(null);

        /*
         * If beneficiary exists but was previously
         * deactivated, reactivate it.
         */
        if (beneficiary != null) {

            if (beneficiary.isActive()) {

                throw new RuntimeException(
                        "Beneficiary already exists"
                );
            }

            beneficiary.setNickname(
                    request.getNickname()
            );

            beneficiary.setActive(true);

        } else {

            beneficiary =
                    new Beneficiary(
                            userId,
                            receiverUserId,
                            request.getNickname()
                    );
        }

        Beneficiary saved =
                beneficiaryRepository.save(
                        beneficiary
                );

        return new BeneficiaryResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<BeneficiaryResponse> getBeneficiaries(
            Long userId
    ) {

        return beneficiaryRepository
                .findByUserIdOrderByCreatedAtDesc(
                        userId
                )
                .stream()
                .map(BeneficiaryResponse::new)
                .toList();
    }

    @Transactional
    public void deleteBeneficiary(
            Long userId,
            Long beneficiaryId
    ) {

        /*
         * findByIdAndUserId ensures that a user
         * can only delete his own beneficiary.
         */
        Beneficiary beneficiary =
                beneficiaryRepository
                        .findByIdAndUserId(
                                beneficiaryId,
                                userId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Beneficiary not found"
                                )
                        );

        beneficiary.setActive(false);

        beneficiaryRepository.save(
                beneficiary
        );
    }
}