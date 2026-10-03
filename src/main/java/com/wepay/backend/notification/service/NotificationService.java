package com.wepay.backend.notification.service;

import com.wepay.backend.notification.dto.NotificationResponse;
import com.wepay.backend.notification.entity.Notification;
import com.wepay.backend.notification.enums.NotificationType;
import com.wepay.backend.notification.repository.NotificationRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(
            NotificationRepository notificationRepository
    ) {
        this.notificationRepository = notificationRepository;
    }

    public void createNotification(
            Long userId,
            NotificationType type,
            String message
    ) {

        Notification notification =
                new Notification(
                        userId,
                        type,
                        message
                );

        notificationRepository.save(notification);
    }

    public List<NotificationResponse> getMyNotifications(
            Long userId
    ) {

        return notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(notification ->
                        new NotificationResponse(
                                notification.getId(),
                                notification.getType().name(),
                                notification.getMessage(),
                                notification.isRead(),
                                notification.getCreatedAt()
                        )
                )
                .toList();
    }

    public void markAsRead(
            Long userId,
            Long notificationId
    ) {

        Notification notification =
                notificationRepository
                        .findByIdAndUserId(
                                notificationId,
                                userId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Notification not found"
                                )
                        );

        notification.setRead(true);

        notificationRepository.save(notification);
    }
}