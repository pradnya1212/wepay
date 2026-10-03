package com.wepay.backend.transaction.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class PaymentRequest {

    @NotNull(message = "Receiver user ID is required")
    private Long receiverUserId;

    @NotNull(message = "Bank account ID is required")
    private Long bankAccountId;

    @NotNull(message = "Amount is required")
    @DecimalMin(
            value = "1.00",
            message = "Amount must be at least 1.00"
    )
    @DecimalMax(
            value = "100000.00",
            message = "Amount cannot exceed 100000.00"
    )
    private BigDecimal amount;

    @NotBlank(message = "Payment PIN is required")
    @Pattern(
            regexp = "^[0-9]{4}$",
            message = "Payment PIN must be exactly 4 digits"
    )
    private String pin;

    @NotBlank(message = "Idempotency key is required")
    @Size(
            max = 100,
            message = "Idempotency key cannot exceed 100 characters"
    )
    private String idempotencyKey;

    public PaymentRequest() {
    }

    public Long getReceiverUserId() {
        return receiverUserId;
    }

    public void setReceiverUserId(Long receiverUserId) {
        this.receiverUserId = receiverUserId;
    }

    public Long getBankAccountId() {
        return bankAccountId;
    }

    public void setBankAccountId(Long bankAccountId) {
        this.bankAccountId = bankAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }
}