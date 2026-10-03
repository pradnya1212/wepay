package com.wepay.backend.upi.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "upi_accounts",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = "upiId"
                )
        }
)
public class UpiAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String upiId;

    @Column(nullable = false)
    private Long bankAccountId;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public UpiAccount() {
    }

    public UpiAccount(
            Long userId,
            String upiId,
            Long bankAccountId
    ) {
        this.userId = userId;
        this.upiId = upiId;
        this.bankAccountId = bankAccountId;
        this.active = true;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}