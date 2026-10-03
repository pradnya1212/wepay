package com.wepay.backend.pin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class PaymentPinRequest {

    @NotBlank(message = "Payment PIN is required")
    @Pattern(
            regexp = "^[0-9]{4}$",
            message = "Payment PIN must be exactly 4 digits"
    )
    private String pin;

    public PaymentPinRequest() {
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
}