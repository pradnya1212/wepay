package com.wepay.backend.user.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String mobileNumber;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private int failedLoginAttempts = 0;

    @Column
    private LocalDateTime accountLockedUntil;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // =========================
    // DEFAULT CONSTRUCTOR
    // =========================

    public User() {
    }

    // =========================
    // CONSTRUCTOR
    // =========================

    public User(
            String name,
            String mobileNumber,
            String email,
            String password
    ) {
        this.name = name;
        this.mobileNumber = mobileNumber;
        this.email = email;
        this.password = password;
        this.role = Role.CUSTOMER;
        this.failedLoginAttempts = 0;
        this.accountLockedUntil = null;
    }

    // =========================
    // GETTERS
    // =========================

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public LocalDateTime getAccountLockedUntil() {
        return accountLockedUntil;
    }

    // =========================
    // SETTERS
    // =========================

    public void setName(String name) {
        this.name = name;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setFailedLoginAttempts(
            int failedLoginAttempts
    ) {
        this.failedLoginAttempts =
                failedLoginAttempts;
    }

    public void setAccountLockedUntil(
            LocalDateTime accountLockedUntil
    ) {
        this.accountLockedUntil =
                accountLockedUntil;
    }
}