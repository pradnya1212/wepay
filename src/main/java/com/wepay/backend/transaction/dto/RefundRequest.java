package com.wepay.backend.transaction.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class RefundRequest {

    @NotBlank(message = "Payment PIN is required")
    @Pattern(
            regexp = "^[0-9]{4}$",
            message = "Payment PIN must be exactly 4 digits"
    )
    private String pin;

    public RefundRequest() {
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
}