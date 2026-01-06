package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

// Firebase Auth imports are no longer needed
// import com.google.firebase.auth.FirebaseAuth;
// import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddTransactionActivity extends AppCompatActivity {

    private EditText etAmount, etCategory, etNote;
    private RadioGroup rgType;
    private RadioButton rbIncome, rbExpense;
    private Button btnSave;

    // FirebaseFirestore is still needed to save the data
    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;

    private static final String PREFS_NAME = "UserPrefs";
    private static final String USER_ID_KEY = "userId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        // Initialize Firestore DB and SharedPreferences
        db = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        etAmount = findViewById(R.id.etAmount);
        etCategory = findViewById(R.id.etCategory);
        etNote = findViewById(R.id.etNote);
        rgType = findViewById(R.id.rgType);
        rbIncome = findViewById(R.id.rbIncome);
        rbExpense = findViewById(R.id.rbExpense);
        btnSave = findViewById(R.id.btnSave);

        btnSave.setOnClickListener(v -> saveTransaction());
    }

    private void saveTransaction() {

        String amountStr = etAmount.getText().toString().trim();
        String category = etCategory.getText().toString().trim();
        String note = etNote.getText().toString().trim();

        if (amountStr.isEmpty() || category.isEmpty()) {
            Toast.makeText(this, "Amount and Category are required", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        String type = rbIncome.isChecked() ? "Income" : "Expense";
        String date = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date());

        // Get userId from SharedPreferences instead of FirebaseAuth
        String userId = sharedPreferences.getString(USER_ID_KEY, null);
        if (userId == null) {
            Toast.makeText(this, "User not logged in. Cannot save transaction.", Toast.LENGTH_SHORT).show();
            // You might want to redirect to LoginActivity here
            return;
        }

        // --- NEW: Show a toast when the save process starts ---
        Toast.makeText(this, "Adding Transaction...", Toast.LENGTH_SHORT).show();

        Map<String, Object> transaction = new HashMap<>();
        transaction.put("userId", userId);
        transaction.put("amount", amount);
        transaction.put("type", type);
        transaction.put("category", category);
        transaction.put("note", note);
        transaction.put("date", date);

        db.collection("transactions")
                .add(transaction)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Transaction Added Successfully", Toast.LENGTH_SHORT).show();

                    // --- NEW: Auto-clear the form on success ---
                    clearForm();

                    // We finish the activity to go back to the main screen
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to add transaction", Toast.LENGTH_SHORT).show());
    }

    // --- NEW: A helper method to clear all input fields ---
    private void clearForm() {
        etAmount.setText("");
        etCategory.setText("");
        etNote.setText("");
        rbIncome.setChecked(true); // Reset radio group to default
        etAmount.requestFocus();   // Set focus back to the first field for the next entry
    }
}
