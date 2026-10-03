package com.wepay.backend.bank.controller;

import com.wepay.backend.bank.dto.BankAccountResponse;
import com.wepay.backend.bank.dto.LinkBankAccountRequest;
import com.wepay.backend.bank.entity.BankAccount;
import com.wepay.backend.bank.service.BankAccountService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bank/accounts")
public class BankAccountController {

    private final BankAccountService bankAccountService;

    public BankAccountController(
            BankAccountService bankAccountService
    ) {
        this.bankAccountService = bankAccountService;
    }

    @PostMapping("/link")
    public ResponseEntity<BankAccountResponse> linkBankAccount(
            Authentication authentication,
            @Valid @RequestBody LinkBankAccountRequest request
    ) {

        Long userId =
                (Long) authentication.getPrincipal();

        BankAccount account =
                bankAccountService.linkBankAccount(
                        userId,
                        request
                );

        BankAccountResponse response =
                new BankAccountResponse(
                        account.getId(),
                        account.getBankName(),
                        maskAccountNumber(
                                account.getAccountNumber()
                        ),
                        account.getAccountHolderName(),
                        account.isVerified(),
                        account.isPrimaryAccount()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<BankAccountResponse>> getMyBankAccounts(
            Authentication authentication
    ) {

        Long userId =
                (Long) authentication.getPrincipal();

        List<BankAccount> accounts =
                bankAccountService.getUserBankAccounts(
                        userId
                );

        List<BankAccountResponse> response =
                accounts.stream()
                        .map(account ->
                                new BankAccountResponse(
                                        account.getId(),
                                        account.getBankName(),
                                        maskAccountNumber(
                                                account.getAccountNumber()
                                        ),
                                        account.getAccountHolderName(),
                                        account.isVerified(),
                                        account.isPrimaryAccount()
                                )
                        )
                        .toList();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBankAccount(
            Authentication authentication,
            @PathVariable Long id
    ) {

        Long userId =
                (Long) authentication.getPrincipal();

        bankAccountService.deleteBankAccount(
                userId,
                id
        );

        return ResponseEntity.ok(
                "Bank account unlinked successfully"
        );
    }

    private String maskAccountNumber(
            String accountNumber
    ) {

        if (accountNumber == null ||
                accountNumber.length() < 4) {

            return "XXXX";
        }

        return "XXXX XXXX " +
                accountNumber.substring(
                        accountNumber.length() - 4
                );
    }
}