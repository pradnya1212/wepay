package com.wepay.backend.otp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class OtpVerifyRequest {

    @NotBlank
    private String otp;

    public OtpVerifyRequest() {
    }

    @Pattern(regexp = "\\d{6}")
    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}