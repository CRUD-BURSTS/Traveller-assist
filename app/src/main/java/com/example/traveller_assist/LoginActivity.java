package com.example.traveller_assist;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText emailOrUsername, password;
    private Button loginButton, signupButton;
    private TextView subscriptionLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize views
        emailOrUsername = findViewById(R.id.emailOrUsername);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        signupButton = findViewById(R.id.signupButton);
        subscriptionLink = findViewById(R.id.subscriptionLink);

        // Login Button Click Listener
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailOrUsername.getText().toString().trim();
                String pass = password.getText().toString().trim();

                // Validate input
                if (email.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else if (!isValidEmail(email) && !isValidUsername(email)) {
                    // Check if the input is a valid email or username
                    Toast.makeText(LoginActivity.this, "Invalid email or username", Toast.LENGTH_SHORT).show();
                } else if (!isValidPassword(pass)) {
                    // Check if the password meets the requirements
                    Toast.makeText(LoginActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                } else {
                    // Perform login logic (e.g., API call)
                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Signup Button Click Listener
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Signup Activity
                Toast.makeText(LoginActivity.this, "Navigate to Signup", Toast.LENGTH_SHORT).show();
            }
        });

        // Subscription Link Click Listener
        subscriptionLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open subscription page (e.g., browser or new activity)
                Toast.makeText(LoginActivity.this, "Open Subscription Page", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to validate email
    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern);
    }

    // Method to validate username (alphanumeric and underscores)
    private boolean isValidUsername(String username) {
        String usernamePattern = "^[a-zA-Z0-9_]+$";
        return username.matches(usernamePattern);
    }

    // Method to validate password (at least 6 characters)
    private boolean isValidPassword(String password) {
        return password.length() >= 6;
    }
}