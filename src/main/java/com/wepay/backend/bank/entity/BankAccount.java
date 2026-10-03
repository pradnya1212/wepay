package com.wepay.backend.bank.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "bank_accounts")
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String bankName;

    @Column(nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private String accountHolderName;

    @Column(nullable = false)
    private boolean verified;

    @Column(nullable = false)
    private boolean primaryAccount;

    @Column(nullable = false)
    private LocalDateTime linkedAt;

    public BankAccount() {
    }

    public BankAccount(
            Long userId,
            String bankName,
            String accountNumber,
            String accountHolderName
    ) {
        this.userId = userId;
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.verified = false;
        this.primaryAccount = false;
        this.linkedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getBankName() {
        return bankName;
    }

    public String getAccountNumber() {
        return accountNumber;
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

    public LocalDateTime getLinkedAt() {
        return linkedAt;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public void setPrimaryAccount(boolean primaryAccount) {
        this.primaryAccount = primaryAccount;
    }
}