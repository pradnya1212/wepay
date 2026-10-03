package com.wepay.backend.security;

import com.wepay.backend.user.entity.Role;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final String secret =
            "WePaySuperSecretKeyForJwtAuthentication2026SecureKey";

    private final long expirationTime =
            1000 * 60 * 60; // 1 hour

    private SecretKey getSigningKey() {

        return Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }

    // =========================
    // GENERATE JWT TOKEN
    // =========================

    public String generateToken(
            Long userId,
            String mobileNumber,
            Role role) {

        Date now = new Date();

        Date expiration =
                new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("mobileNumber", mobileNumber)
                .claim("role", role.name())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSigningKey())
                .compact();
    }

    // =========================
    // EXTRACT USER ID
    // =========================

    public Long extractUserId(String token) {

        String subject =
                Jwts.parser()
                        .verifyWith(getSigningKey())
                        .build()
                        .parseSignedClaims(token)
                        .getPayload()
                        .getSubject();

        return Long.parseLong(subject);
    }

    // =========================
    // EXTRACT ROLE
    // =========================

    public String extractRole(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
    }

    // =========================
    // CHECK TOKEN EXPIRATION
    // =========================

    public boolean isTokenExpired(String token) {

        Date expiration =
                Jwts.parser()
                        .verifyWith(getSigningKey())
                        .build()
                        .parseSignedClaims(token)
                        .getPayload()
                        .getExpiration();

        return expiration.before(new Date());
    }
}