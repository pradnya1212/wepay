package com.wepay.backend.beneficiary.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AddBeneficiaryRequest {

    @NotNull
    private Long receiverUserId;

    @NotBlank
    @Size(min = 2, max = 100)
    private String nickname;

    public AddBeneficiaryRequest() {
    }

    public Long getReceiverUserId() {
        return receiverUserId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setReceiverUserId(Long receiverUserId) {
        this.receiverUserId = receiverUserId;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}