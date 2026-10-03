package com.wepay.backend.user.controller;

import com.wepay.backend.user.dto.LoginRequest;
import com.wepay.backend.user.dto.LoginResponse;
import com.wepay.backend.user.dto.LoginResult;
import com.wepay.backend.user.dto.RegisterRequest;
import com.wepay.backend.user.dto.RegisterResponse;
import com.wepay.backend.user.dto.UserProfileResponse;
import com.wepay.backend.user.entity.User;
import com.wepay.backend.user.service.UserService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // =========================
    // REGISTER
    // =========================

    @PostMapping("/auth/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        User user = userService.registerUser(request);

        RegisterResponse response =
                new RegisterResponse(
                        user.getId(),
                        user.getName(),
                        user.getMobileNumber(),
                        user.getEmail(),
                        user.getRole().name(),
                        "User registered successfully"
                );
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // =========================
    // LOGIN
    // =========================

    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        LoginResult result =
                userService.loginUser(request);

        User user = result.getUser();

        LoginResponse response =
                new LoginResponse(
                        "Login successful",
                        result.getToken(),
                        user.getId(),
                        user.getName(),
                        user.getMobileNumber()
                );

        return ResponseEntity.ok(response);
    }

    // =========================
    // CURRENT USER
    // =========================

    @GetMapping("/users/me")
    public ResponseEntity<UserProfileResponse> getCurrentUser(
            Authentication authentication) {

        Long userId = (Long) authentication.getPrincipal();

        User user = userService.getUserById(userId);

        UserProfileResponse response =
                new UserProfileResponse(
                        user.getId(),
                        user.getName(),
                        user.getMobileNumber(),
                        user.getEmail()
                );

        return ResponseEntity.ok(response);
    }

    // =========================
    // DELETE USER
    // =========================

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(
            @PathVariable Long id) {

        userService.deleteUser(id);

        return ResponseEntity.ok(
                "User deleted successfully"
        );
    }
}