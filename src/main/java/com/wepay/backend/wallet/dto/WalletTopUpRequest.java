package com.wepay.backend.wallet.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class WalletTopUpRequest {

    @NotNull(message = "Amount is required")
    @DecimalMin(
            value = "1.00",
            message = "Top-up amount must be at least 1.00"
    )
    @DecimalMax(
            value = "100000.00",
            message = "Top-up amount cannot exceed 100000.00"
    )
    private BigDecimal amount;

    public WalletTopUpRequest() {
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}