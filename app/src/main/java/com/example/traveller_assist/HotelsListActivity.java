package com.example.traveller_assist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class HotelsListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_list); // Ensure this matches your XML layout file name

        // Find the CardView by its ID
        CardView cardView = findViewById(R.id.cardViewHotel1);

        // Set an OnClickListener on the CardView
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to navigate to HotelDetailsActivity
                Intent intent = new Intent(HotelsListActivity.this, HotelDetailsActivity.class);

                // Optionally, pass data to the HotelDetailsActivity using intent extras


                // Start the HotelDetailsActivity
                startActivity(intent);
            }
        });
    }
}