package com.wepay.backend.upi.dto;

public class UpiAccountResponse {

    private Long id;
    private String upiId;
    private Long bankAccountId;
    private boolean active;

    public UpiAccountResponse(
            Long id,
            String upiId,
            Long bankAccountId,
            boolean active
    ) {
        this.id = id;
        this.upiId = upiId;
        this.bankAccountId = bankAccountId;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public String getUpiId() {
        return upiId;
    }

    public Long getBankAccountId() {
        return bankAccountId;
    }

    public boolean isActive() {
        return active;
    }
}