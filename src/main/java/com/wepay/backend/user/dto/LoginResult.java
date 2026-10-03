package com.wepay.backend.user.dto;

import com.wepay.backend.user.entity.User;

public class LoginResult {

    private User user;
    private String token;

    public LoginResult(User user, String token) {
        this.user = user;
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public String getToken() {
        return token;
    }
}