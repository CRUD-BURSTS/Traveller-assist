package com.example.traveller_assist;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class HotelDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hoteldetails); // Ensure this matches your XML layout file name

        // Initialize views
        ImageView hotelImageView = findViewById(R.id.hotelImageView);
        TextView hotelNameTextView = findViewById(R.id.hotelNameTextView);
        TextView hotelAddressTextView = findViewById(R.id.hotelAddressTextView);
        TextView hotelDescriptionTextView = findViewById(R.id.hotelDescriptionTextView);
        ImageView googleMapImageView = findViewById(R.id.googleMapImageView);

        // Get data passed from HotelsListActivity
        Intent intent = getIntent();

        // Enable the back button in the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Handle the back button click
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        // Navigate back to HotelsListActivity
        super.onBackPressed();
        Intent intent = new Intent(this, HotelsListActivity.class);
        startActivity(intent);
        finish(); // Close the current activity
    }
}
