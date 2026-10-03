package com.wepay.backend.device.service;

import com.wepay.backend.device.dto.DeviceResponse;
import com.wepay.backend.device.entity.UserDevice;
import com.wepay.backend.device.repository.UserDeviceRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DeviceService {

    private final UserDeviceRepository deviceRepository;

    public DeviceService(
            UserDeviceRepository deviceRepository
    ) {
        this.deviceRepository = deviceRepository;
    }

    @Transactional
    public DeviceResponse registerDevice(
            Long userId,
            String deviceId,
            String deviceName,
            String platform
    ) {

        UserDevice device =
                deviceRepository
                        .findByUserIdAndDeviceId(
                                userId,
                                deviceId
                        )
                        .orElse(null);

        if (device == null) {

            device =
                    new UserDevice(
                            userId,
                            deviceId,
                            deviceName,
                            platform
                    );

        } else {

            device.setDeviceName(deviceName);
            device.setPlatform(platform);
            device.setActive(true);
            device.setLastSeenAt(
                    LocalDateTime.now()
            );
        }

        UserDevice saved =
                deviceRepository.save(device);

        return new DeviceResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<DeviceResponse> getUserDevices(
            Long userId
    ) {

        return deviceRepository
                .findByUserIdOrderByLastSeenAtDesc(
                        userId
                )
                .stream()
                .map(DeviceResponse::new)
                .toList();
    }

    @Transactional
    public void revokeDevice(
            Long userId,
            Long deviceId
    ) {

        UserDevice device =
                deviceRepository
                        .findByIdAndUserId(
                                deviceId,
                                userId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Device not found"
                                )
                        );

        device.setActive(false);

        deviceRepository.save(device);
    }
}