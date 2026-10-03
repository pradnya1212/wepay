package com.wepay.backend.device.dto;

import com.wepay.backend.device.entity.UserDevice;

import java.time.LocalDateTime;

public class DeviceResponse {

    private Long id;
    private String deviceName;
    private String platform;
    private boolean active;
    private LocalDateTime firstRegisteredAt;
    private LocalDateTime lastSeenAt;

    public DeviceResponse(UserDevice device) {

        this.id = device.getId();
        this.deviceName = device.getDeviceName();
        this.platform = device.getPlatform();
        this.active = device.isActive();
        this.firstRegisteredAt =
                device.getFirstRegisteredAt();
        this.lastSeenAt =
                device.getLastSeenAt();
    }

    public Long getId() {
        return id;
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
}