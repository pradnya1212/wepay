package com.wepay.backend.requestmoney.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class AcceptMoneyRequest {

    @NotNull
    private Long bankAccountId;

    @NotBlank
    @Pattern(regexp = "\\d{4}")
    private String pin;

    @NotBlank
    private String idempotencyKey;

    public AcceptMoneyRequest() {
    }

    public Long getBankAccountId() {
        return bankAccountId;
    }

    public String getPin() {
        return pin;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setBankAccountId(Long bankAccountId) {
        this.bankAccountId = bankAccountId;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }
}