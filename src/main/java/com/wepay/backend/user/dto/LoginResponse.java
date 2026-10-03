package com.wepay.backend.user.dto;

public class LoginResponse {

    private String message;
    private String token;
    private Long userId;
    private String name;
    private String mobileNumber;

    public LoginResponse() {
    }

    public LoginResponse(
            String message,
            String token,
            Long userId,
            String name,
            String mobileNumber
    ) {
        this.message = message;
        this.token = token;
        this.userId = userId;
        this.name = name;
        this.mobileNumber = mobileNumber;
    }

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }

    public Long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }
}