package com.wepay.backend.device.controller;

import com.wepay.backend.device.dto.DeviceResponse;
import com.wepay.backend.device.service.DeviceService;

import jakarta.validation.constraints.NotBlank;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(
            DeviceService deviceService
    ) {
        this.deviceService = deviceService;
    }

    @PostMapping("/register")
    public ResponseEntity<DeviceResponse> registerDevice(
            Authentication authentication,

            @RequestParam
            @NotBlank
            String deviceId,

            @RequestParam
            @NotBlank
            String deviceName,

            @RequestParam(required = false)
            String platform
    ) {

        Long userId =
                (Long) authentication.getPrincipal();

        DeviceResponse response =
                deviceService.registerDevice(
                        userId,
                        deviceId,
                        deviceName,
                        platform
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<DeviceResponse>> getDevices(
            Authentication authentication
    ) {

        Long userId =
                (Long) authentication.getPrincipal();

        return ResponseEntity.ok(
                deviceService.getUserDevices(
                        userId
                )
        );
    }

    @DeleteMapping("/{deviceId}")
    public ResponseEntity<Map<String, String>> revokeDevice(
            Authentication authentication,
            @PathVariable Long deviceId
    ) {

        Long userId =
                (Long) authentication.getPrincipal();

        deviceService.revokeDevice(
                userId,
                deviceId
        );

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Device revoked successfully"
                )
        );
    }
}