package com.wepay.backend.bank.provider;

import com.wepay.backend.bank.entity.BankAccount;

import java.util.List;

public interface BankProvider {

    List<BankAccount> getAccounts(String mobileNumber);

    boolean verifyAccount(BankAccount account);
}