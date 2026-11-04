package com.example.myapplication.manager;

import com.example.myapplication.exceptions.AccountNotFoundException;
import com.example.myapplication.exceptions.InvalidAmountException;
import com.example.myapplication.model.BankAccount;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class BankManager {
    private final Map<String, BankAccount> accounts;

    public BankManager() {
        this.accounts = new LinkedHashMap<>();
    }

    public void addAccount(BankAccount account) throws InvalidAmountException {
        if (account == null) {
            throw new InvalidAmountException("חשבון לא תקין");
        }
        if (accounts.containsKey(account.getAccountNumber())) {
            throw new InvalidAmountException("מספר חשבון זה כבר קיים במערכת");
        }
        accounts.put(account.getAccountNumber(), account);
    }

    public BankAccount getAccount(String accountNumber) throws AccountNotFoundException {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new AccountNotFoundException("יש להזין מספר חשבון יעד");
        }
        BankAccount account = accounts.get(accountNumber.trim());
        if (account == null) {
            throw new AccountNotFoundException("חשבון יעד " + accountNumber + " לא נמצא במערכת");
        }
        return account;
    }

    public ArrayList<BankAccount> getAllAccounts() {
        return new ArrayList<>(accounts.values());
    }

    public int getAccountCount() {
        return accounts.size();
    }
}
