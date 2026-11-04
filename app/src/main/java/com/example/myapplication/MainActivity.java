package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.exceptions.AccountBlockedException;
import com.example.myapplication.exceptions.AccountNotFoundException;
import com.example.myapplication.exceptions.InsufficientFundsException;
import com.example.myapplication.exceptions.InvalidAmountException;
import com.example.myapplication.manager.BankManager;
import com.example.myapplication.model.BankAccount;
import com.example.myapplication.model.Transaction;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private BankManager bankManager;
    private BankAccount currentAccount;

    // Main UI components
    private Spinner spinnerAccounts;
    private TextView textViewAccountDetails;
    private Button buttonToggleBlock;
    private EditText editTextAmount;
    private EditText editTextTargetAccount;
    private Button buttonDeposit;
    private Button buttonWithdraw;
    private Button buttonTransfer;

    private EditText editTextTransactionIndex;
    private Button buttonViewTransaction;
    private TextView textViewTransactionResult;
    private TextView textViewHistoryList;

    private EditText editTextNewAccountNum;
    private EditText editTextNewOwnerName;
    private EditText editTextNewInitialBalance;
    private Button buttonCreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initBankManager();
        setupAccountSpinner();
        setupListeners();
    }

    private void initViews() {
        spinnerAccounts = findViewById(R.id.spinnerAccounts);
        textViewAccountDetails = findViewById(R.id.textViewAccountDetails);
        buttonToggleBlock = findViewById(R.id.buttonToggleBlock);
        editTextAmount = findViewById(R.id.editTextAmount);
        editTextTargetAccount = findViewById(R.id.editTextTargetAccount);
        buttonDeposit = findViewById(R.id.buttonDeposit);
        buttonWithdraw = findViewById(R.id.buttonWithdraw);
        buttonTransfer = findViewById(R.id.buttonTransfer);

        editTextTransactionIndex = findViewById(R.id.editTextTransactionIndex);
        buttonViewTransaction = findViewById(R.id.buttonViewTransaction);
        textViewTransactionResult = findViewById(R.id.textViewTransactionResult);
        textViewHistoryList = findViewById(R.id.textViewHistoryList);

        editTextNewAccountNum = findViewById(R.id.editTextNewAccountNum);
        editTextNewOwnerName = findViewById(R.id.editTextNewOwnerName);
        editTextNewInitialBalance = findViewById(R.id.editTextNewInitialBalance);
        buttonCreateAccount = findViewById(R.id.buttonCreateAccount);
    }

    private void initBankManager() {
        bankManager = new BankManager();
        try {
            bankManager.addAccount(new BankAccount("1001", "Israel Israeli", 1500.00));
            bankManager.addAccount(new BankAccount("1002", "Danny Cohen", 500.00));
            bankManager.addAccount(new BankAccount("1003", "Sarah Levi", 2500.00));
        } catch (InvalidAmountException ignored) {
        }
    }

    private void setupAccountSpinner() {
        ArrayList<BankAccount> accounts = bankManager.getAllAccounts();
        ArrayAdapter<BankAccount> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                accounts
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAccounts.setAdapter(adapter);

        if (!accounts.isEmpty()) {
            currentAccount = accounts.get(0);
            updateUI();
        }

        spinnerAccounts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentAccount = (BankAccount) parent.getItemAtPosition(position);
                updateUI();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupListeners() {
        buttonToggleBlock.setOnClickListener(v -> handleToggleBlock());
        buttonDeposit.setOnClickListener(v -> handleDeposit());
        buttonWithdraw.setOnClickListener(v -> handleWithdraw());
        buttonTransfer.setOnClickListener(v -> handleTransfer());
        buttonViewTransaction.setOnClickListener(v -> handleViewTransactionByIndex());
        buttonCreateAccount.setOnClickListener(v -> handleCreateAccount());
    }

    private void handleToggleBlock() {
        if (currentAccount == null) return;
        boolean newStatus = !currentAccount.isBlocked();
        currentAccount.setBlocked(newStatus);
        Toast.makeText(this, newStatus ? "Account is now blocked" : "Account unblocked", Toast.LENGTH_SHORT).show();
        updateUI();
    }

    private void handleDeposit() {
        if (currentAccount == null) return;
        try {
            double amount = Double.parseDouble(editTextAmount.getText().toString());
            currentAccount.deposit(amount);
            Toast.makeText(this, "Deposit successful", Toast.LENGTH_SHORT).show();
            updateUI();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
        } catch (InvalidAmountException | AccountBlockedException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            editTextAmount.setText("");
        }
    }

    private void handleWithdraw() {
        if (currentAccount == null) return;
        try {
            double amount = Double.parseDouble(editTextAmount.getText().toString());
            currentAccount.withdraw(amount);
            Toast.makeText(this, "Withdrawal successful", Toast.LENGTH_SHORT).show();
            updateUI();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
        } catch (InsufficientFundsException | InvalidAmountException | AccountBlockedException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            editTextAmount.setText("");
        }
    }

    private void handleTransfer() {
        if (currentAccount == null) return;
        try {
            double amount = Double.parseDouble(editTextAmount.getText().toString());
            String targetAccountNumber = editTextTargetAccount.getText().toString().trim();

            BankAccount targetAccount = bankManager.getAccount(targetAccountNumber);
            currentAccount.transfer(targetAccount, amount);

            Toast.makeText(this, "Transfer successful", Toast.LENGTH_SHORT).show();
            updateUI();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
        } catch (InsufficientFundsException | InvalidAmountException | AccountNotFoundException | AccountBlockedException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            editTextAmount.setText("");
            editTextTargetAccount.setText("");
        }
    }

    private void handleViewTransactionByIndex() {
        if (currentAccount == null) return;
        try {
            int index = Integer.parseInt(editTextTransactionIndex.getText().toString());
            ArrayList<Transaction> history = currentAccount.getTransactionHistory();
            if (index < 0 || index >= history.size()) {
                throw new IndexOutOfBoundsException("Index out of bounds (0 - " + (history.isEmpty() ? 0 : history.size() - 1) + ")");
            }
            Transaction t = history.get(index);
            textViewTransactionResult.setText("Transaction at [" + index + "]:\n" + t.toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid index number", Toast.LENGTH_SHORT).show();
        } catch (IndexOutOfBoundsException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            editTextTransactionIndex.setText("");
        }
    }

    private void handleCreateAccount() {
        try {
            String accNum = editTextNewAccountNum.getText().toString().trim();
            String owner = editTextNewOwnerName.getText().toString().trim();
            double initial = Double.parseDouble(editTextNewInitialBalance.getText().toString().trim());

            if (accNum.isEmpty() || owner.isEmpty()) {
                throw new InvalidAmountException("Please fill all fields to create an account");
            }

            BankAccount newAccount = new BankAccount(accNum, owner, initial);
            bankManager.addAccount(newAccount);
            Toast.makeText(this, "New account created successfully", Toast.LENGTH_SHORT).show();
            setupAccountSpinner();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid initial amount", Toast.LENGTH_SHORT).show();
        } catch (InvalidAmountException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            editTextNewAccountNum.setText("");
            editTextNewOwnerName.setText("");
            editTextNewInitialBalance.setText("");
        }
    }

    private void updateUI() {
        if (currentAccount == null) return;

        textViewAccountDetails.setText(currentAccount.printDetails());

        ArrayList<Transaction> history = currentAccount.getTransactionHistory();
        if (history == null || history.isEmpty()) {
            textViewHistoryList.setText("No transactions");
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < history.size(); i++) {
                sb.append("[").append(i).append("] ").append(history.get(i).toString()).append("\n");
            }
            textViewHistoryList.setText(sb.toString().trim());
        }

        if (spinnerAccounts.getAdapter() instanceof ArrayAdapter) {
            ((ArrayAdapter<?>) spinnerAccounts.getAdapter()).notifyDataSetChanged();
        }
    }
}
