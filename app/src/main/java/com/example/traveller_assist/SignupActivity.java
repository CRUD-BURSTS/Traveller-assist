package com.example.traveller_assist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale;

public class SignupActivity extends AppCompatActivity {

    // UI Elements
    private TextInputEditText editTextFullName, editTextEmail, editTextPassword, editTextConfirmPassword, editTextPhone;
    private AutoCompleteTextView autoCompleteLocation;
    private MaterialButton buttonSignup;
    private TextView textViewLogin;

    private DBHelper dbHelper; // Database Helper

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize UI Components
        editTextFullName = findViewById(R.id.editTextFullName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        editTextPhone = findViewById(R.id.editTextPhone);
        autoCompleteLocation = findViewById(R.id.autoCompleteLocation);
        buttonSignup = findViewById(R.id.buttonSignup);
        textViewLogin = findViewById(R.id.textViewLogin);

        dbHelper = new DBHelper(this); // Initialize DBHelper

        // Initialize AutoComplete Location
        setupAutoCompleteLocation();

        // Handle Sign-Up Button Click
        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSignup();
            }
        });

        // Redirect to Login Activity
        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    // Function to handle sign-up logic
    private void handleSignup() {
        String fullName = editTextFullName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String location = autoCompleteLocation.getText().toString().trim();

        // Basic Validation
        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || phone.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if email already exists
        if (dbHelper.checkEmailExists(email)) {
            Toast.makeText(this, "Email already exists!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Insert the user data into the database
        long result = dbHelper.insertUser(fullName, email, password, phone, location);
        if (result != -1) {
            Toast.makeText(this, "Account Created Successfully!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SignupActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Error occurred. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    // Setup AutoCompleteTextView with a list of all countries
    private void setupAutoCompleteLocation() {
        // Get all country names from the system locale
        String[] countryList = Locale.getISOCountries();
        String[] countryNames = new String[countryList.length];

        for (int i = 0; i < countryList.length; i++) {
            Locale locale = new Locale("", countryList[i]);
            countryNames[i] = locale.getDisplayCountry();
        }

        // Set up the adapter for AutoCompleteTextView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, countryNames);
        autoCompleteLocation.setAdapter(adapter);
    }
}
