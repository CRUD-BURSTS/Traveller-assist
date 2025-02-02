package com.example.traveller_assist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Hide the action bar if you want a fullscreen splash
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    // Handle click anywhere on the screen
    public void onScreenClick(View view) {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // This prevents going back to splash screen when pressing back
    }
}
