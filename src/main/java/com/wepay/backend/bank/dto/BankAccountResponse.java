package com.wepay.backend.bank.dto;

public class BankAccountResponse {

    private Long id;
    private String bankName;
    private String maskedAccountNumber;
    private String accountHolderName;
    private boolean verified;
    private boolean primaryAccount;

    public BankAccountResponse(
            Long id,
            String bankName,
            String maskedAccountNumber,
            String accountHolderName,
            boolean verified,
            boolean primaryAccount
    ) {
        this.id = id;
        this.bankName = bankName;
        this.maskedAccountNumber = maskedAccountNumber;
        this.accountHolderName = accountHolderName;
        this.verified = verified;
        this.primaryAccount = primaryAccount;
    }

    public Long getId() {
        return id;
    }

    public String getBankName() {
        return bankName;
    }

    public String getMaskedAccountNumber() {
        return maskedAccountNumber;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public boolean isVerified() {
        return verified;
    }

    public boolean isPrimaryAccount() {
        return primaryAccount;
    }
}