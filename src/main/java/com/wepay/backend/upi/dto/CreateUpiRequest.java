package com.wepay.backend.upi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class CreateUpiRequest {

    @NotBlank(message = "UPI ID is required")
    @Pattern(
            regexp = "^[a-zA-Z0-9._-]{3,30}@wepay$",
            message = "UPI ID must be in format username@wepay"
    )
    private String upiId;

    @NotNull(message = "Bank account ID is required")
    private Long bankAccountId;

    public CreateUpiRequest() {
    }

    public String getUpiId() {
        return upiId;
    }

    public void setUpiId(String upiId) {
        this.upiId = upiId;
    }

    public Long getBankAccountId() {
        return bankAccountId;
    }

    public void setBankAccountId(Long bankAccountId) {
        this.bankAccountId = bankAccountId;
    }
}