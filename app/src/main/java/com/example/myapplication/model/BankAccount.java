package com.example.myapplication.model;

import androidx.annotation.NonNull;

import com.example.myapplication.exceptions.AccountBlockedException;
import com.example.myapplication.exceptions.AccountNotFoundException;
import com.example.myapplication.exceptions.InsufficientFundsException;
import com.example.myapplication.exceptions.InvalidAmountException;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class BankAccount {
    private String accountNumber;
    private String ownerName;
    private double balance;
    private boolean isBlocked;
    private final ArrayList<Transaction> transactionHistory;

    public BankAccount(String accountNumber, String ownerName, double initialBalance) throws InvalidAmountException {
        if (initialBalance < 0) {
            throw new InvalidAmountException("סכום הפקדה ראשוני לא יכול להיות שלילי");
        }
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.balance = initialBalance;
        this.isBlocked = false;
        this.transactionHistory = new ArrayList<>();
        if (initialBalance > 0) {
            transactionHistory.add(new Transaction(
                    Transaction.Type.DEPOSIT,
                    initialBalance,
                    accountNumber,
                    null,
                    "הפקדה ראשונית"
            ));
        }
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public double getBalance() {
        return balance;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public ArrayList<Transaction> getTransactionHistory() {
        return transactionHistory;
    }

    public void deposit(double amount) throws InvalidAmountException, AccountBlockedException {
        if (isBlocked) {
            throw new AccountBlockedException("החשבון חסום לביצוע פעולות");
        }
        if (amount <= 0) {
            throw new InvalidAmountException("סכום ההפקדה חייב להיות חיובי");
        }
        balance += amount;
        transactionHistory.add(new Transaction(
                Transaction.Type.DEPOSIT,
                amount,
                accountNumber,
                null,
                "הפקדה לחשבון"
        ));
    }

    public void withdraw(double amount) throws InsufficientFundsException, InvalidAmountException, AccountBlockedException {
        if (isBlocked) {
            throw new AccountBlockedException("החשבון חסום לביצוע פעולות");
        }
        if (amount <= 0) {
            throw new InvalidAmountException("סכום המשיכה חייב להיות חיובי");
        }
        if (amount > balance) {
            throw new InsufficientFundsException("אין מספיק כסף בחשבון");
        }
        balance -= amount;
        transactionHistory.add(new Transaction(
                Transaction.Type.WITHDRAW,
                amount,
                accountNumber,
                null,
                "משיכה מהחשבון"
        ));
    }

    public void transfer(BankAccount target, double amount) throws InsufficientFundsException, InvalidAmountException, AccountNotFoundException, AccountBlockedException {
        if (isBlocked) {
            throw new AccountBlockedException("החשבון שלך חסום לביצוע העברות");
        }
        if (target == null) {
            throw new AccountNotFoundException("חשבון היעד לא קיים");
        }
        if (target.isBlocked()) {
            throw new AccountBlockedException("חשבון היעד חסום לביצוע פעולות");
        }
        if (Objects.equals(target.getAccountNumber(), this.accountNumber)) {
            throw new InvalidAmountException("לא ניתן להעביר כסף לאותו החשבון");
        }
        this.withdraw(amount);
        try {
            target.deposit(amount);
        } catch (InvalidAmountException | AccountBlockedException e) {
            this.balance += amount;
            throw e;
        }

        this.transactionHistory.add(new Transaction(
                Transaction.Type.TRANSFER,
                amount,
                this.accountNumber,
                target.getAccountNumber(),
                "העברה לחשבון: " + target.getAccountNumber()
        ));

        target.getTransactionHistory().add(new Transaction(
                Transaction.Type.TRANSFER,
                amount,
                this.accountNumber,
                target.getAccountNumber(),
                "העברה מכל חשבון: " + this.accountNumber
        ));
    }

    public String printDetails() {
        return "מספר חשבון: " + accountNumber + "\n" +
               "שם בעל החשבון: " + ownerName + "\n" +
               "יתרה נוכחית: ₪" + String.format(Locale.getDefault(), "%.2f", balance) + "\n" +
               "סטטוס: " + (isBlocked ? "🔒 חסום" : "✅ פעיל");
    }

    @NonNull
    @Override
    public String toString() {
        return ownerName + " (" + accountNumber + ") - ₪" + String.format(Locale.getDefault(), "%.2f", balance) + (isBlocked ? " [חסום]" : "");
    }
}
