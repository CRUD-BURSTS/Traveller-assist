/*package com.example.traveller_assist;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

public class TimeActivity extends Activity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private TextView timeDisplay, locationText, currencyText;
    private FusedLocationProviderClient fusedLocationClient;
    private final OkHttpClient client = new OkHttpClient();

    // Add your API keys here
    private static final String TIMEZONE_API_KEY = "X49V22S53HU4";
    private static final String CURRENCY_API_KEY = "c6d0485789178b4787c22f7a";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);

        // Initialize views
        initializeViews();

        // Initialize location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Check and request permissions
        checkLocationPermission();
    }

    private void initializeViews() {
        timeDisplay = findViewById(R.id.timeDisplay);
        locationText = findViewById(R.id.location);
        currencyText = findViewById(R.id.currency);
        Button backButton = findViewById(R.id.backButton);
        Button nextButton = findViewById(R.id.nextButton);
        Button hotelsButton = findViewById(R.id.hotelsButton);

        backButton.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));
        nextButton.setOnClickListener(v -> startActivity(new Intent(this, HotelActivity.class)));
        hotelsButton.setOnClickListener(v -> startActivity(new Intent(this, HotelActivity.class)));
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getCurrentLocation();
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            CancellationTokenSource cts = new CancellationTokenSource();
            Task<Location> locationTask = fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, cts.getToken());

            locationTask.addOnSuccessListener(this, location -> {
                if (location != null) {
                    updateLocationInfo(location);
                } else {
                    Toast.makeText(this, "Failed to get location!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateLocationInfo(Location location) {
        // Get location details using Geocoder
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(
                    location.getLatitude(), location.getLongitude(), 1);

            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                String locationString = String.format("%s, %s",
                        address.getLocality(),
                        address.getCountryName());
                locationText.setText(locationString);

                // Get timezone and local time
                getTimeZoneAndTime(location.getLatitude(), location.getLongitude());

                // Get local currency
                getCurrencyForCountry(address.getCountryCode());
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error getting location details", Toast.LENGTH_SHORT).show();
        }
    }

    private void getTimeZoneAndTime(double latitude, double longitude) {
        new Thread(() -> {
            try {
                // Using TimeZoneDB API
                String url = String.format("http://api.timezonedb.com/v2.1/get-time-zone?" +
                                "key=%s&format=json&by=position&lat=%f&lng=%f",
                        TIMEZONE_API_KEY, latitude, longitude);

                Request request = new Request.Builder().url(url).build();
                Response response = client.newCall(request).execute();

                if (response.isSuccessful() && response.body() != null) {
                    JSONObject jsonResponse = new JSONObject(response.body().string());
                    if (jsonResponse.has("zoneName")) {
                        String timezone = jsonResponse.getString("zoneName");

                        // Format the time according to the timezone
                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                        sdf.setTimeZone(TimeZone.getTimeZone(timezone));
                        String localTime = sdf.format(new Date());

                        runOnUiThread(() -> timeDisplay.setText(localTime));
                    } else {
                        runOnUiThread(() -> Toast.makeText(this, "Failed to retrieve timezone", Toast.LENGTH_SHORT).show());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void getCurrencyForCountry(String countryCode) {
        new Thread(() -> {
            try {
                // Using ExchangeRate API (base currency as USD)
                String url = String.format("https://v6.exchangerate-api.com/v6/%s/latest/USD", CURRENCY_API_KEY);

                Request request = new Request.Builder().url(url).build();
                Response response = client.newCall(request).execute();

                if (response.isSuccessful() && response.body() != null) {
                    JSONObject jsonResponse = new JSONObject(response.body().string());

                    if (jsonResponse.has("conversion_rates")) {
                        JSONObject rates = jsonResponse.getJSONObject("conversion_rates");

                        if (rates.has(countryCode)) {
                            double exchangeRate = rates.getDouble(countryCode);
                            runOnUiThread(() -> currencyText.setText(String.format("Currency: %s (1 USD = %.2f %s)",
                                    countryCode, exchangeRate, countryCode)));
                        } else {
                            runOnUiThread(() -> currencyText.setText("Currency not available"));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Permission denied! Location features won't work.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}*/
package com.example.traveller_assist;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.util.Date;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import java.io.IOException;

public class TimeActivity extends Activity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private TextView timeDisplay, locationText, currencyText;
    private FusedLocationProviderClient fusedLocationClient;
    private final OkHttpClient client = new OkHttpClient();

    // Add your API keys here
    private static final String TIMEZONE_API_KEY = "X49V22S53HU4";
    private static final String CURRENCY_API_KEY = "c6d0485789178b4787c22f7a";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);

        // Initialize views
        initializeViews();

        // Initialize location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Check and request permissions
        checkLocationPermission();
    }

    private void initializeViews() {
        timeDisplay = findViewById(R.id.timeDisplay);
        locationText = findViewById(R.id.location);
        currencyText = findViewById(R.id.currency);
        Button backButton = findViewById(R.id.backButton);
        Button nextButton = findViewById(R.id.nextButton);
        Button hotelsButton = findViewById(R.id.hotelsButton);

        backButton.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));
        nextButton.setOnClickListener(v -> startActivity(new Intent(this, HotelsListActivity.class)));
        hotelsButton.setOnClickListener(v -> startActivity(new Intent(this, HotelsListActivity.class)));
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getLastLocation();
        }
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            updateLocationInfo(location);
                        }
                    });
        }
    }

    private void updateLocationInfo(Location location) {
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(
                    location.getLatitude(), location.getLongitude(), 1);

            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                String locationString = String.format("%s, %s",
                        address.getLocality(),
                        address.getCountryName());
                locationText.setText(locationString);

                // Get timezone and local time
                getTimeZoneAndTime(location.getLatitude(), location.getLongitude());

                // Get local currency
                getCurrencyForCountry(address.getCountryCode());
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error getting location details", Toast.LENGTH_SHORT).show();
        }
    }

    private void getTimeZoneAndTime(double latitude, double longitude) {
        new Thread(() -> {
            try {
                String url = String.format("http://api.timezonedb.com/v2.1/get-time-zone?" +
                                "key=%s&format=json&by=position&lat=%f&lng=%f",
                        TIMEZONE_API_KEY, latitude, longitude);

                Request request = new Request.Builder().url(url).build();
                Response response = client.newCall(request).execute();

                if (response.isSuccessful() && response.body() != null) {
                    JSONObject jsonResponse = new JSONObject(response.body().string());

                    if (jsonResponse.has("zoneName")) {
                        String timezone = jsonResponse.getString("zoneName");

                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                        sdf.setTimeZone(TimeZone.getTimeZone(timezone));
                        String localTime = sdf.format(new Date());

                        runOnUiThread(() -> timeDisplay.setText(localTime));
                    } else {
                        runOnUiThread(() -> Toast.makeText(this, "Error fetching timezone", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Timezone API Error", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Failed to fetch timezone", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void getCurrencyForCountry(String countryCode) {
        new Thread(() -> {
            try {
                String currencyCode = getCurrencyCode(countryCode);
                if (currencyCode == null) {
                    runOnUiThread(() -> currencyText.setText("Currency data unavailable"));
                    return;
                }

                String url = String.format("https://v6.exchangerate-api.com/v6/%s/latest/USD", CURRENCY_API_KEY);

                Request request = new Request.Builder().url(url).build();
                Response response = client.newCall(request).execute();

                if (response.isSuccessful() && response.body() != null) {
                    JSONObject jsonResponse = new JSONObject(response.body().string());

                    if (jsonResponse.has("conversion_rates")) {
                        JSONObject rates = jsonResponse.getJSONObject("conversion_rates");

                        if (rates.has(currencyCode)) {
                            double exchangeRate = rates.getDouble(currencyCode);
                            runOnUiThread(() -> currencyText.setText(
                                    String.format("Currency: %s (1 USD = %.2f %s)", currencyCode, exchangeRate, currencyCode)
                            ));
                        } else {
                            runOnUiThread(() -> currencyText.setText("Currency data unavailable"));
                        }
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Currency API Error", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Failed to fetch currency data", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private String getCurrencyCode(String countryCode) {
        switch (countryCode) {
            case "US": return "USD";
            case "LK": return "LKR";
            case "IN": return "INR";
            case "GB": return "GBP";
            case "JP": return "JPY";
            case "AU": return "AUD";
            case "EU": return "EUR";
            case "CA": return "CAD";
            case "CN": return "CNY";
            default: return null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(this, "Permission denied! Location features won't work.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
