package com.wepay.backend.upi.controller;

import com.wepay.backend.upi.dto.CreateUpiRequest;
import com.wepay.backend.upi.dto.UpiAccountResponse;
import com.wepay.backend.upi.entity.UpiAccount;
import com.wepay.backend.upi.service.UpiAccountService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/upi")
public class UpiAccountController {

    private final UpiAccountService upiAccountService;

    public UpiAccountController(
            UpiAccountService upiAccountService
    ) {
        this.upiAccountService = upiAccountService;
    }

    @PostMapping("/create")
    public ResponseEntity<UpiAccountResponse> createUpiId(
            Authentication authentication,
            @Valid @RequestBody CreateUpiRequest request
    ) {

        Long userId =
                (Long) authentication.getPrincipal();

        UpiAccount upiAccount =
                upiAccountService.createUpiId(
                        userId,
                        request
                );

        UpiAccountResponse response =
                new UpiAccountResponse(
                        upiAccount.getId(),
                        upiAccount.getUpiId(),
                        upiAccount.getBankAccountId(),
                        upiAccount.isActive()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<UpiAccountResponse>> getMyUpiAccounts(
            Authentication authentication
    ) {

        Long userId =
                (Long) authentication.getPrincipal();

        List<UpiAccount> accounts =
                upiAccountService
                        .getMyUpiAccounts(userId);

        List<UpiAccountResponse> response =
                accounts.stream()
                        .map(account ->
                                new UpiAccountResponse(
                                        account.getId(),
                                        account.getUpiId(),
                                        account.getBankAccountId(),
                                        account.isActive()
                                )
                        )
                        .toList();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deactivateUpiId(
            Authentication authentication,
            @PathVariable Long id
    ) {

        Long userId =
                (Long) authentication.getPrincipal();

        upiAccountService.deactivateUpiId(
                userId,
                id
        );

        return ResponseEntity.ok(
                "UPI ID deactivated successfully"
        );
    }
}