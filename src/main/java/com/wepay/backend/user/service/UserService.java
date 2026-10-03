package com.wepay.backend.user.service;

import com.wepay.backend.security.JwtService;
import com.wepay.backend.user.dto.LoginRequest;
import com.wepay.backend.user.dto.LoginResult;
import com.wepay.backend.user.dto.RegisterRequest;
import com.wepay.backend.user.entity.User;
import com.wepay.backend.user.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserService {

    private static final int MAX_FAILED_ATTEMPTS = 5;

    private static final long LOCK_DURATION_MINUTES = 24L * 60L;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // =====================================================
    // REGISTER
    // =====================================================

    @Transactional
    public User registerUser(
            RegisterRequest request
    ) {

        if (userRepository.existsByMobileNumber(
                request.getMobileNumber()
        )) {

            throw new RuntimeException(
                    "Mobile number already registered"
            );
        }

        String hashedPassword =
                passwordEncoder.encode(
                        request.getPassword()
                );

        User user =
                new User(
                        request.getName(),
                        request.getMobileNumber(),
                        request.getEmail(),
                        hashedPassword
                );

        return userRepository.save(user);
    }

    // =====================================================
    // LOGIN
    // =====================================================

    @Transactional
    public LoginResult loginUser(
            LoginRequest request
    ) {

        User user =
                userRepository
                        .findByMobileNumber(
                                request.getMobileNumber()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Invalid mobile number or password"
                                )
                        );

        // -------------------------------------------------
        // CHECK ACCOUNT LOCK
        // -------------------------------------------------

        if (isAccountLocked(user)) {

            throw new RuntimeException(
                    "Account temporarily locked. Please try again later."
            );
        }

        // -------------------------------------------------
        // CHECK PASSWORD
        // -------------------------------------------------

        boolean passwordMatches =
                passwordEncoder.matches(
                        request.getPassword(),
                        user.getPassword()
                );

        // -------------------------------------------------
        // WRONG PASSWORD
        // -------------------------------------------------

        if (!passwordMatches) {

            handleFailedLogin(user);

            throw new RuntimeException(
                    "Invalid mobile number or password"
            );
        }

        // -------------------------------------------------
        // SUCCESSFUL LOGIN
        // -------------------------------------------------

        resetFailedLoginAttempts(user);

        String token =
                jwtService.generateToken(
                        user.getId(),
                        user.getMobileNumber(),
                        user.getRole()
                );

        return new LoginResult(
                user,
                token
        );
    }

    // =====================================================
    // CHECK ACCOUNT LOCK
    // =====================================================

    private boolean isAccountLocked(
            User user
    ) {

        LocalDateTime lockedUntil =
                user.getAccountLockedUntil();

        if (lockedUntil == null) {

            return false;
        }

        // Lock period expired
        if (LocalDateTime.now()
                .isAfter(lockedUntil)) {

            user.setAccountLockedUntil(null);
            user.setFailedLoginAttempts(0);

            userRepository.save(user);

            return false;
        }

        return true;
    }

    // =====================================================
    // HANDLE FAILED LOGIN
    // =====================================================

    private void handleFailedLogin(
            User user
    ) {

        int attempts =
                user.getFailedLoginAttempts() + 1;

        user.setFailedLoginAttempts(attempts);

        if (attempts >= MAX_FAILED_ATTEMPTS) {

            LocalDateTime lockUntil =
                    LocalDateTime.now()
                            .plusMinutes(
                                    LOCK_DURATION_MINUTES
                            );

            user.setAccountLockedUntil(
                    lockUntil
            );

            user.setFailedLoginAttempts(0);
        }

        userRepository.save(user);
    }

    // =====================================================
    // RESET FAILED ATTEMPTS
    // =====================================================

    private void resetFailedLoginAttempts(
            User user
    ) {

        user.setFailedLoginAttempts(0);
        user.setAccountLockedUntil(null);

        userRepository.save(user);
    }

    // =====================================================
    // GET USER
    // =====================================================

    public User getUserById(
            Long id
    ) {

        return userRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        )
                );
    }

    // =====================================================
    // DELETE USER
    // =====================================================

    @Transactional
    public void deleteUser(
            Long id
    ) {

        if (!userRepository.existsById(id)) {

            throw new RuntimeException(
                    "User not found"
            );
        }

        userRepository.deleteById(id);
    }
}