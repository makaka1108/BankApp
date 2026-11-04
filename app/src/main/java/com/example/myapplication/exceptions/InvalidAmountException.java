package com.example.myapplication.exceptions;

public class InvalidAmountException extends BankOperationException {
    public InvalidAmountException(String message) {
        super(message);
    }
}
