package com.wepay.backend.requestmoney.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class CreateMoneyRequest {

    @NotNull
    private Long receiverUserId;

    @NotNull
    @DecimalMin(value = "1.00")
    @DecimalMax(value = "100000.00")
    private BigDecimal amount;

    @Size(max = 200)
    private String note;

    public CreateMoneyRequest() {
    }

    public Long getReceiverUserId() {
        return receiverUserId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getNote() {
        return note;
    }

    public void setReceiverUserId(Long receiverUserId) {
        this.receiverUserId = receiverUserId;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setNote(String note) {
        this.note = note;
    }
}