package com.wepay.backend.otp.dto;

import jakarta.validation.constraints.NotNull;

public class OtpRequest {

    @NotNull
    private Long userId;

    public OtpRequest() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}