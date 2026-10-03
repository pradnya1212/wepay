package com.wepay.backend.beneficiary.dto;

import com.wepay.backend.beneficiary.entity.Beneficiary;

import java.time.LocalDateTime;

public class BeneficiaryResponse {

    private Long id;
    private Long receiverUserId;
    private String nickname;
    private boolean active;
    private LocalDateTime createdAt;

    public BeneficiaryResponse(Beneficiary beneficiary) {

        this.id = beneficiary.getId();
        this.receiverUserId =
                beneficiary.getReceiverUserId();
        this.nickname =
                beneficiary.getNickname();
        this.active =
                beneficiary.isActive();
        this.createdAt =
                beneficiary.getCreatedAt();
    }

    public Long getId() {
        return id;
    }

    public Long getReceiverUserId() {
        return receiverUserId;
    }

    public String getNickname() {
        return nickname;
    }

    public boolean isActive() {
        return active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}