package com.wepay.backend.user.dto;

public class UserProfileResponse {

    private Long id;
    private String name;
    private String mobileNumber;
    private String email;

    public UserProfileResponse(
            Long id,
            String name,
            String mobileNumber,
            String email
    ) {
        this.id = id;
        this.name = name;
        this.mobileNumber = mobileNumber;
        this.email = email;
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
}