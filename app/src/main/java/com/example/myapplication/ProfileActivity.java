package com.example.myapplication;

import android.content.Context;import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

// Firebase imports are no longer needed
// import com.google.firebase.auth.FirebaseAuth;
// import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUsername; // Changed from tvEmail
    private EditText etNewPassword;
    private Button btnChangePassword, btnLogout;

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "UserPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Views
        tvUsername = findViewById(R.id.tvEmail); // The ID is still tvEmail in your XML, but it will show username
        etNewPassword = findViewById(R.id.etNewPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnLogout = findViewById(R.id.btnLogout);

        // Show Username from SharedPreferences
        String username = sharedPreferences.getString("username", "User");
        tvUsername.setText("Username: " + username);

        // Change Password
        btnChangePassword.setOnClickListener(v -> changePassword());

        // Logout
        btnLogout.setOnClickListener(v -> {
            // Get an editor to modify SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();

            // Clear all user-specific data
            editor.remove("isLoggedIn");
            editor.remove("userId");
            // You can use editor.clear() if you want to wipe everything in this file
            editor.apply();

            // Go back to LoginActivity and clear the activity stack
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void changePassword() {
        String newPassword = etNewPassword.getText().toString().trim();

        if (newPassword.isEmpty() || newPassword.length() < 6) {
            etNewPassword.setError("Enter minimum 6 characters");
            return;
        }

        // Save the new password to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("password", newPassword);
        editor.apply();

        Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show();
        etNewPassword.setText(""); // Clear the field after updating
    }
}
