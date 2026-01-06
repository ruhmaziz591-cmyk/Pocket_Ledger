package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    EditText etNewUsername, etNewPassword;
    Button btnSave;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etNewUsername = findViewById(R.id.etEmail);
        etNewPassword = findViewById(R.id.etPassword);
        btnSave = findViewById(R.id.btnRegister);

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        btnSave.setOnClickListener(v -> {

            String newUsername = etNewUsername.getText().toString().trim();
            String newPassword = etNewPassword.getText().toString().trim();

            if (newUsername.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString("username", newUsername);
            editor.putString("password", newPassword);

            // 🔑 VERY IMPORTANT
            editor.putBoolean("isLoggedIn", false);

            editor.apply();

            Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show();
            finish(); // back to LoginActivity
        });
    }
}
