package com.wepay.backend.user.dto;

public class RegisterResponse {

    private Long id;
    private String name;
    private String mobileNumber;
    private String email;
    private String role;
    private String message;

    public RegisterResponse() {
    }

    public RegisterResponse(
            Long id,
            String name,
            String mobileNumber,
            String email,
            String role,
            String message
    ) {
        this.id = id;
        this.name = name;
        this.mobileNumber = mobileNumber;
        this.email = email;
        this.role = role;
        this.message = message;
    }

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

    public String getRole() {
        return role;
    }

    public String getMessage() {
        return message;
    }
}