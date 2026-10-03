package com.wepay.backend.notification.controller;

import com.wepay.backend.notification.dto.NotificationResponse;
import com.wepay.backend.notification.service.NotificationService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(
            NotificationService notificationService
    ) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getMyNotifications(
            Authentication authentication
    ) {

        Long userId =
                (Long) authentication.getPrincipal();

        return ResponseEntity.ok(
                notificationService.getMyNotifications(
                        userId
                )
        );
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<String> markAsRead(
            Authentication authentication,
            @PathVariable Long id
    ) {

        Long userId =
                (Long) authentication.getPrincipal();

        notificationService.markAsRead(
                userId,
                id
        );

        return ResponseEntity.ok(
                "Notification marked as read"
        );
    }
}