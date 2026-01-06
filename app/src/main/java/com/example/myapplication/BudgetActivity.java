package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

// Removed all Firebase imports

public class BudgetActivity extends AppCompatActivity {

    private EditText etMonthlyBudget;
    private Button btnSaveBudget;

    // SharedPreferences for storing the budget
    private SharedPreferences sharedPreferences;

    // Define a name for the preferences file and a key for the budget value
    private static final String PREFS_NAME = "BudgetPrefs";
    private static final String BUDGET_KEY = "monthlyBudget";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Initialize views
        etMonthlyBudget = findViewById(R.id.etMonthlyBudget);
        btnSaveBudget = findViewById(R.id.btnSaveBudget);

        // Load the previously saved budget, if any
        loadBudget();

        // Set the click listener to save the new budget
        btnSaveBudget.setOnClickListener(v -> saveBudget());
    }


    private void saveBudget() {
        String budgetStr = etMonthlyBudget.getText().toString().trim();

        if (budgetStr.isEmpty()) {
            Toast.makeText(this, "Enter monthly budget", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // It's good practice to use float for money, or a dedicated money library
            float budgetValue = Float.parseFloat(budgetStr);

            // Get an editor to write to SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();

            // Save the budget value as a float
            editor.putFloat(BUDGET_KEY, budgetValue);

            // Apply the changes to save the file
            editor.apply();

            Toast.makeText(this, "Budget saved successfully", Toast.LENGTH_SHORT).show();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadBudget() {
        // Load the budget from SharedPreferences. Default to 0.0f if not found.
        float savedBudget = sharedPreferences.getFloat(BUDGET_KEY, 0.0f);

        // If a budget was saved previously (i.e., it's not the default 0.0), display it.
        if (savedBudget > 0.0f) {
            etMonthlyBudget.setText(String.valueOf(savedBudget));
        }
    }
}
