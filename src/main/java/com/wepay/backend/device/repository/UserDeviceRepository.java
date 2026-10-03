package com.wepay.backend.device.repository;

import com.wepay.backend.device.entity.UserDevice;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserDeviceRepository
        extends JpaRepository<UserDevice, Long> {

    List<UserDevice> findByUserIdOrderByLastSeenAtDesc(
            Long userId
    );

    Optional<UserDevice> findByUserIdAndDeviceId(
            Long userId,
            String deviceId
    );

    Optional<UserDevice> findByIdAndUserId(
            Long id,
            Long userId
    );
}