package com.example.myapplication.exceptions;

public class AccountNotFoundException extends BankOperationException {
    public AccountNotFoundException(String message) {
        super(message);
    }
}
