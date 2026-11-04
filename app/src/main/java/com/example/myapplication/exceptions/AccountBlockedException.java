package com.example.myapplication.exceptions;

public class AccountBlockedException extends BankOperationException {
    public AccountBlockedException(String message) {
        super(message);
    }
}
