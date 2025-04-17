package com.example.carbotrackphoneapplication;

//Java code for the plan your ride page.

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class PlanYourRideActivity extends AppCompatActivity{
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private EditText currentLocationText;
    private EditText destinationSearchText;
    private RecyclerView predictionsRecyclerView;
    private FusedLocationProviderClient locationClient;
    private double userLat, userLon;
    private PredictionAdapter adapter;
    private RequestQueue requestQueue;
    private String userState = "";

    private android.os.Handler debounceHandler = new android.os.Handler();
    private Runnable workRunnable;
    private final int debounceDelay = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planyourride);

        currentLocationText = findViewById(R.id.currentLocationText);
        destinationSearchText = findViewById(R.id.destinationSearchText);
        predictionsRecyclerView = findViewById(R.id.predictionsRecyclerView);

        locationClient = LocationServices.getFusedLocationProviderClient(this);
        requestQueue = Volley.newRequestQueue(this);

        predictionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PredictionAdapter(new ArrayList<>(), (name, lat, lon) -> {
            Intent intent = new Intent(this, ChooseRideActivity.class);
            intent.putExtra("userLat", userLat);
            intent.putExtra("userLon", userLon);
            intent.putExtra("destLat", lat);
            intent.putExtra("destLon", lon);
            intent.putExtra("userName", name);
            startActivity(intent);

        });
        predictionsRecyclerView.setAdapter(adapter);

        getCurrentLocation();

        destinationSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (workRunnable != null)
                    debounceHandler.removeCallbacks(workRunnable);

                workRunnable = () -> fetchPredictions(s.toString(), true);
                debounceHandler.postDelayed(workRunnable, debounceDelay);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
           ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
           return;
        }

        locationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null)
            {
                userLat = location.getLatitude();
                userLon = location.getLongitude();
                fetchAddress(location);
            }
        });
    }

    private void fetchAddress(Location location) {
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (!addresses.isEmpty())
            {
                Address addr = addresses.get(0);
                String address = addr.getAddressLine(0);
                userState = addr.getAdminArea();
                currentLocationText.setText(address);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Here is the location search logic
    private void fetchPredictions(String query, boolean useViewbox) {
        if (query.isEmpty())
        {
            adapter.updateData(new ArrayList<>());
            return;
        }

        // This is roughly a 20 mile radius from the user
        double radius = 0.3;

        double lonMin = userLon - radius;
        double latMax = userLat + radius;
        double lonMax = userLon + radius;
        double latMin = userLat - radius;

        String url = "https://nominatim.openstreetmap.org/search?q=" + query + "&format=json&addressdetails=1" + "&autocomplete=1";

        if (useViewbox)
        {
            url += "&viewbox=" + lonMin + "," + latMax + "," + lonMax + "," + latMin + "&bounded=1" + "&countrycodes=us";
        }

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            List<Prediction> nearbyPredictions = new ArrayList<>();
            List<Prediction> farPredictions = new ArrayList<>();
            List<Prediction> otherPredictions = new ArrayList<>();

            for (int i = 0; i < response.length(); i++)
            {
                try {
                    JSONObject obj = response.getJSONObject(i);
                    String name = obj.getString("display_name");
                    double lat = obj.getDouble("lat");
                    double lon = obj.getDouble("lon");

                    float[] result = new float[1];
                    Location.distanceBetween(userLat, userLon, lat, lon, result);
                    float miles = result[0] / 1609;

                    String state = "";
                    if (obj.has("address"))
                    {
                        JSONObject addressObj = obj.getJSONObject("address");
                        if (addressObj.has("state"))
                        {
                            state = addressObj.getString("state");
                        }
                    }

                    Prediction prediction = new Prediction(name, lat, lon, String.format("%.2f miles away", miles));

                    if (miles <= 50)
                    {
                        nearbyPredictions.add(prediction);

                    }
                    else if (state.equalsIgnoreCase(userState))
                    {
                        farPredictions.add(prediction);
                    }
                    else
                    {
                        otherPredictions.add(prediction);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

            }

            if (response.length() == 0 && useViewbox) {
                fetchPredictions(query, false);
                return;
            }

            if (nearbyPredictions.isEmpty() && farPredictions.isEmpty() && otherPredictions.isEmpty() && !useViewbox)
            {
                Toast.makeText(this, "No Locations were found.", Toast.LENGTH_SHORT).show();
            }

            Collections.sort(nearbyPredictions, Comparator.comparingDouble(p -> {
                String distanceText = p.getDistance().split(" ")[0];
                return Double.parseDouble(distanceText);
            }));

            List<Prediction> finalList = new ArrayList<>();
            finalList.addAll(nearbyPredictions);
            finalList.addAll(farPredictions);
            finalList.addAll(otherPredictions);

            adapter.updateData(finalList);
        }, error -> {
            error.printStackTrace();
            Toast.makeText(this, "Network Error. Check your internet connection.", Toast.LENGTH_SHORT).show();
        });

        requestQueue.add(request);


    }




}
