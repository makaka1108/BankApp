package com.example.myapplication.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Transaction {
    public enum Type {
        DEPOSIT,
        WITHDRAW,
        TRANSFER
    }

    private final Type type;
    private final double amount;
    private final String sourceAccountNumber;
    private final String targetAccountNumber;
    private final String description;
    private final String timestamp;

    public Transaction(Type type, double amount, String sourceAccountNumber, String targetAccountNumber, String description) {
        this.type = type;
        this.amount = amount;
        this.sourceAccountNumber = sourceAccountNumber;
        this.targetAccountNumber = targetAccountNumber;
        this.description = description;
        this.timestamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    public Type getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public String getSourceAccountNumber() {
        return sourceAccountNumber;
    }

    public String getTargetAccountNumber() {
        return targetAccountNumber;
    }

    public String getDescription() {
        return description;
    }

    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(timestamp).append("] ");
        sb.append(type == Type.DEPOSIT ? "הפקדה" : type == Type.WITHDRAW ? "משיכה" : "העברה");
        sb.append(": ₪").append(String.format(Locale.getDefault(), "%.2f", amount));
        if (description != null && !description.isEmpty()) {
            sb.append(" (").append(description).append(")");
        }
        return sb.toString();
    }
}
