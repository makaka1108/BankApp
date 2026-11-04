package com.example.myapplication.exceptions;

public class InsufficientFundsException extends BankOperationException {
    public InsufficientFundsException(String message) {
        super(message);
    }
}
