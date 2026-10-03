package com.wepay.backend.bank.provider;

import com.wepay.backend.bank.entity.BankAccount;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MockBankProvider implements BankProvider {

    @Override
    public List<BankAccount> getAccounts(String mobileNumber) {

        List<BankAccount> accounts = new ArrayList<>();

        if ("9123456789".equals(mobileNumber)) {

            accounts.add(
                    new BankAccount(
                            null,
                            "HDFC Bank",
                            "123456789012",
                            "Customer Test"
                    )
            );

            accounts.add(
                    new BankAccount(
                            null,
                            "State Bank of India",
                            "987654321098",
                            "Customer Test"
                    )
            );
        }

        return accounts;
    }

    @Override
    public boolean verifyAccount(BankAccount account) {

        return account != null
                && account.getAccountNumber() != null
                && account.getAccountNumber().length() == 12;
    }
}