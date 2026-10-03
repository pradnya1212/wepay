package com.wepay.backend.device.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "user_devices",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_device",
                        columnNames = {"user_id", "device_id"}
                )
        }
)
public class UserDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "device_id", nullable = false, length = 255)
    private String deviceId;

    @Column(nullable = false, length = 100)
    private String deviceName;

    @Column(length = 50)
    private String platform;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private LocalDateTime firstRegisteredAt;

    @Column(nullable = false)
    private LocalDateTime lastSeenAt;

    public UserDevice() {
    }

    public UserDevice(
            Long userId,
            String deviceId,
            String deviceName,
            String platform
    ) {
        this.userId = userId;
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.platform = platform;
        this.active = true;
        this.firstRegisteredAt = LocalDateTime.now();
        this.lastSeenAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getPlatform() {
        return platform;
    }

    public boolean isActive() {
        return active;
    }

    public LocalDateTime getFirstRegisteredAt() {
        return firstRegisteredAt;
    }

    public LocalDateTime getLastSeenAt() {
        return lastSeenAt;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setLastSeenAt(LocalDateTime lastSeenAt) {
        this.lastSeenAt = lastSeenAt;
    }
}