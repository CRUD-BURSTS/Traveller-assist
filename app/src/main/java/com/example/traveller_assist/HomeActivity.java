package com.example.traveller_assist;



import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class HomeActivity extends AppCompatActivity {

    private TextView tvTime, tvLocationCurrency;
    private FusedLocationProviderClient fusedLocationClient;
    private Handler timeHandler = new Handler();
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        tvTime = findViewById(R.id.tvTime);
        tvLocationCurrency = findViewById(R.id.tvLocationCurrency);
        ImageView notificationIcon = findViewById(R.id.notificationIcon);
        ImageView profilePhoto = findViewById(R.id.profilePhoto);

        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Fetch location and update time/currency
        requestLocation();

        // Update time every second
        timeHandler.postDelayed(timeRunnable, 1000);

        // Add click listeners (customize as needed)
        notificationIcon.setOnClickListener(v -> showToast("Notifications clicked"));
        profilePhoto.setOnClickListener(v -> showToast("Profile photo clicked"));
    }

    // Runnable to update time
    private Runnable timeRunnable = new Runnable() {
        @Override
        public void run() {
            updateTime();
            timeHandler.postDelayed(this, 1000);
        }
    };

    // Request location permission
    private void requestLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE
            );
        } else {
            fetchLocation();
        }
    }

    // Handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocation();
            } else {
                showToast("Location permission denied");
            }
        }
    }

    // Fetch device location
    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            // Get timezone and currency based on location
                            TimeZone timeZone = TimeZone.getDefault();
                            String currencyCode = Currency.getInstance(Locale.getDefault()).getCurrencyCode();
                            String locationText = "Lat: " + location.getLatitude() + ", Lon: " + location.getLongitude();

                            // Update UI
                            tvLocationCurrency.setText(String.format(
                                    "%s (%s)",
                                    locationText,
                                    currencyCode
                            ));
                        }
                    }
                });
    }

    // Update time based on device timezone
    private void updateTime() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String currentTime = timeFormat.format(new Date());
        tvTime.setText(currentTime);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timeHandler.removeCallbacks(timeRunnable);
    }
}
