package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu; // Import Menu
import android.view.MenuItem; // Import MenuItem
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull; // Import NonNull
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar; // <-- IMPORT THE TOOLBAR CLASS
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // UI elements
    TextView tvWelcome, tvBalanceSummary;
    FloatingActionButton fabAdd;
    RecyclerView recyclerView;

    SharedPreferences sharedPreferences;

    // RecyclerView components
    private TransactionAdapter transactionAdapter;
    private ArrayList<TransactionModel> transactionList;
    private FirebaseFirestore db;

    @Override
    protected void onStart() {
        super.onStart();
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (!isLoggedIn) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // --- NEW: Set up the Toolbar ---
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // --- End of Toolbar Setup ---

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // Initialize views from the layout
        tvWelcome = findViewById(R.id.tvWelcome);
        tvBalanceSummary = findViewById(R.id.tvBalanceSummary);
        fabAdd = findViewById(R.id.fabAdd);
        recyclerView = findViewById(R.id.recyclerView);

        // --- Setup for RecyclerView ---
        db = FirebaseFirestore.getInstance();
        transactionList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        transactionAdapter = new TransactionAdapter(this, transactionList);
        recyclerView.setAdapter(transactionAdapter);
        setupDeleteListener();
        // --- End of RecyclerView Setup ---

        // Set Welcome message
        String username = sharedPreferences.getString("username", "User");
        tvWelcome.setText("Welcome, " + username);

        // Load data from Firestore
        loadTransactions();

        // --- Set OnClick Listener for FAB ---
        fabAdd.setOnClickListener(v -> startActivity(new Intent(this, AddTransactionActivity.class)));
    }

    // This method will now correctly inflate the menu into the Toolbar you just set
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    // --- Method to handle menu item clicks ---
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_budget) {
            startActivity(new Intent(this, BudgetActivity.class));
            return true;
        } else if (id == R.id.menu_analytics) {
            startActivity(new Intent(this, AnalyticsActivity.class));
            return true;
        } else if (id == R.id.menu_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        } else if (id == R.id.menu_logout) {
            logoutUser();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // --- Extracted logout logic into its own method ---
    private void logoutUser() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear().apply(); // Clear all shared preferences on logout

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void loadTransactions() {
        String userId = sharedPreferences.getString("userId", null);
        if (userId == null) {
            Toast.makeText(this, "User not found, cannot load transactions.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("transactions")
                .whereEqualTo("userId", userId)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    transactionList.clear();
                    double totalIncome = 0;
                    double totalExpense = 0;

                    for (var doc : queryDocumentSnapshots) {
                        TransactionModel model = doc.toObject(TransactionModel.class);
                        model.setId(doc.getId());
                        transactionList.add(model);

                        if (model.getType().equalsIgnoreCase("Income")) {
                            totalIncome += model.getAmount();
                        } else {
                            totalExpense += model.getAmount();
                        }
                    }
                    transactionAdapter.notifyDataSetChanged();

                    double balance = totalIncome - totalExpense;
                    String balanceText = String.format(Locale.getDefault(), "Current Balance: Rs %.2f", balance);
                    tvBalanceSummary.setText(balanceText);

                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load transactions.", Toast.LENGTH_SHORT).show());
    }

    private void setupDeleteListener() {
        transactionAdapter.setOnDeleteClickListener(position -> {
            TransactionModel transactionToDelete = transactionList.get(position);
            String docId = transactionToDelete.getId();

            if (docId == null || docId.isEmpty()) {
                Toast.makeText(this, "Error: Cannot delete item without ID.", Toast.LENGTH_SHORT).show();
                return;
            }

            new AlertDialog.Builder(this)
                    .setTitle("Delete Transaction")
                    .setMessage("Are you sure you want to delete this transaction?")
                    .setPositiveButton("Delete", (dialog, which) -> deleteTransactionFromFirestore(docId, position))
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void deleteTransactionFromFirestore(String docId, int position) {
        db.collection("transactions").document(docId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Transaction deleted", Toast.LENGTH_SHORT).show();
                    loadTransactions();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to delete transaction", Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            loadTransactions();
        }
    }
}
