package com.example.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

// Import for SharedPreferences, FirebaseAuth is no longer needed
// All Firebase imports have been removed.

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText emailEditText; // In a SharedPreferences system, this can be used to look up the username
    private Button resetPasswordButton;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailEditText = findViewById(R.id.emailEditText);
        resetPasswordButton = findViewById(R.id.resetPasswordButton);

        // Initialize SharedPreferences with the same name as in LoginActivity
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // We'll treat the "email" field as the username field for lookup
                String usernameToFind = emailEditText.getText().toString().trim();

                if (usernameToFind.isEmpty()) {
                    emailEditText.setError("Username required");
                    emailEditText.requestFocus();
                    return;
                }

                // Retrieve the saved username and password from SharedPreferences
                String savedUsername = sharedPreferences.getString("username", "");
                String savedPassword = sharedPreferences.getString("password", "");

                // Check if the entered username matches the one stored on the device
                if (usernameToFind.equals(savedUsername)) {
                    // In a real-world secure app, you would NOT show the password.
                    // For this simple implementation, we'll show it in a Toast message.
                    Toast.makeText(ForgotPasswordActivity.this,
                            "Password recovery: Your password is: " + savedPassword, Toast.LENGTH_LONG).show();
                    finish(); // Close the activity
                } else {
                    Toast.makeText(ForgotPasswordActivity.this,
                            "No user found with that username on this device.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
