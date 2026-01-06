package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

// Import for SharedPreferences, FirebaseAuth is no longer needed here
import com.google.firebase.firestore.FirebaseFirestore;

// Removed FirebaseAuth and FirebaseUser imports

public class AnalyticsActivity extends AppCompatActivity {

    private TextView tvTotalIncome, tvTotalExpense, tvBalance;

    // FirebaseFirestore is still needed to get the transaction data
    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;

    // Define the name for your login preferences file to stay consistent
    // This MUST match the name used in your LoginActivity
    private static final String USER_PREFS_NAME = "UserPrefs";
    private static final String USER_ID_KEY = "userId"; // A key to store the user's Firestore ID

    double totalIncome = 0;
    double totalExpense = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        tvTotalIncome = findViewById(R.id.tvTotalIncome);
        tvTotalExpense = findViewById(R.id.tvTotalExpense);
        tvBalance = findViewById(R.id.tvBalance);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(USER_PREFS_NAME, Context.MODE_PRIVATE);

        // Initialize only Firestore
        db = FirebaseFirestore.getInstance();

        loadAnalytics();
    }

    private void loadAnalytics() {
        // Get the logged-in user's ID from SharedPreferences instead of FirebaseAuth
        String userId = sharedPreferences.getString(USER_ID_KEY, null);

        // If no userId is found, the user is not properly logged in.
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            // Optional: Redirect to LoginActivity
            // startActivity(new Intent(this, LoginActivity.class));
            // finish();
            return;
        }

        // The rest of the logic is the same, using the userId from SharedPreferences
        db.collection("transactions")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    totalIncome = 0;
                    totalExpense = 0;

                    for (var doc : queryDocumentSnapshots) {
                        TransactionModel model = doc.toObject(TransactionModel.class);

                        if (model.getType().equalsIgnoreCase("Income")) {
                            totalIncome += model.getAmount();
                        } else {
                            totalExpense += model.getAmount();
                        }
                    }

                    double balance = totalIncome - totalExpense;

                    // It's better to use String.format for cleaner text
                    tvTotalIncome.setText(String.format("Total Income: Rs %.2f", totalIncome));
                    tvTotalExpense.setText(String.format("Total Expense: Rs %.2f", totalExpense));
                    tvBalance.setText(String.format("Remaining Balance: Rs %.2f", balance));
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load analytics", Toast.LENGTH_SHORT).show());
    }
}
