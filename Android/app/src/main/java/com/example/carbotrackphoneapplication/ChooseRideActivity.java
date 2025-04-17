package com.example.carbotrackphoneapplication;

//Here is the Java code for the choose ride page.
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.osmdroid.api.IMapController;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;

public class ChooseRideActivity extends AppCompatActivity {

    private MapView mapView;
    private RecyclerView rideRecyclerView;
    private double userLat, userLon, destLat, destLon;
    private TextView distanceText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_ride);

        userLat = getIntent().getDoubleExtra("userLat", 0);
        userLon = getIntent().getDoubleExtra("userLon", 0);
        destLat = getIntent().getDoubleExtra("destLat", 0);
        destLon = getIntent().getDoubleExtra("destLon", 0);

        mapView = findViewById(R.id.mapView);
        mapView.setMultiTouchControls(true);
        rideRecyclerView = findViewById(R.id.rideRecyclerView);
        distanceText = findViewById(R.id.distanceText);

        IMapController mapController = mapView.getController();
        mapController.setZoom(15.0);
        mapController.setCenter(new GeoPoint(userLat, userLon));

        Marker startMarker = new Marker(mapView);
        startMarker.setPosition(new GeoPoint(userLat, userLon));
        startMarker.setTitle("Your Location");
        mapView.getOverlays().add(startMarker);

        Marker endMarker = new Marker(mapView);
        endMarker.setPosition(new GeoPoint(destLat, destLon));
        endMarker.setTitle("Destination");
        mapView.getOverlays().add(endMarker);

        Utils.fetchRoute(this, new GeoPoint(userLat, userLon), new GeoPoint(destLat, destLon),
                new Utils.RouteCallback() {
                    @Override
                    public void onRouteReady(List<GeoPoint> routePoints) {
                        Polyline polyline = new Polyline();
                        polyline.setPoints(routePoints);
                        mapView.getOverlays().add(polyline);
                        mapView.invalidate();
                    }

                    @Override
                    public void onError(String message) {
                        Toast.makeText(ChooseRideActivity.this, message, Toast.LENGTH_SHORT).show();

                    }
                });

        float distance = Utils.calculateDistance(userLat, userLon, destLat, destLon);
        distanceText.setText(String.format("Distance: %.2f miles", distance));

        // Here we are predicting the travel time based on the transportation method chosen
        List<RideOption> rides = new ArrayList<>();
        rides.add(new RideOption("Car", estimateTime(distance, 35), Utils.calculateCarbonCredits(distance, "car") + "cc"));
        rides.add(new RideOption("Bike", estimateTime(distance, 12), Utils.calculateCarbonCredits(distance, "bike") + "cc"));
        rides.add(new RideOption("Public Transport", estimateTime(distance, 20), Utils.calculateCarbonCredits(distance, "public transport") + "cc"));
        rides.add(new RideOption("Rideshare", estimateTime(distance, 30), Utils.calculateCarbonCredits(distance, "rideshare") + "cc"));

        rideRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        RideAdapter adapter = new RideAdapter(rides, selectedRide -> {
            Intent intent = new Intent(this, TravelSummaryActivity.class);
            intent.putExtra("userLat", userLat);
            intent.putExtra("userLon", userLon);
            intent.putExtra("destLat", destLat);
            intent.putExtra("destLon", destLon);
            intent.putExtra("rideType", selectedRide.getType());
            intent.putExtra("time", selectedRide.getTime());
            intent.putExtra("distance", distance);
            intent.putExtra("credits", Utils.calculateCarbonCredits(distance, selectedRide.getType()));
            startActivity(intent);
        });
        rideRecyclerView.setAdapter(adapter);
    }

    private String estimateTime(float distance, int averageSpeedMph) {
        float timeHours = distance / averageSpeedMph;
        int timeMinutes = (int) (timeHours * 60);
        return timeMinutes + " mins";
    }
}
