package com.example.carbotrackphoneapplication;
// Java code for the travel summary page

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.api.IMapController;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.List;

public class TravelSummaryActivity extends AppCompatActivity {

    private MapView mapView;
    private double userLat, userLon, destLat, destLon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_summary);

        mapView = findViewById(R.id.mapView);
        mapView.setMultiTouchControls(true);

        userLat = getIntent().getDoubleExtra("userLat", 0);
        userLon = getIntent().getDoubleExtra("userLon", 0);
        destLat = getIntent().getDoubleExtra("destLat", 0);
        destLon = getIntent().getDoubleExtra("destLon", 0);

        IMapController mapController = mapView.getController();
        mapController.setZoom(15.0);
        mapController.setCenter(new GeoPoint(userLat, userLon));

        addMarkersAndRoute();

        String rideType = getIntent().getStringExtra("rideType");
        float distance = getIntent().getFloatExtra("distance", 0);
        String formattedDistance = String.format("%.2f", distance);
        String time = getIntent().getStringExtra("time");
        int credits = getIntent().getIntExtra("credits", 0);

        TextView detailsText = findViewById(R.id.detailsText);
        detailsText.setText("Ride: " + rideType + "\nDistance: " + formattedDistance + " miles\nTime: " + time + "\nCredits: " + credits);

        Button startBtn = findViewById(R.id.startTravelBtn);
        startBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, TravelProcessActivity.class);
            intent.putExtras(getIntent());
            startActivity(intent);
        });
    }

    private void addMarkersAndRoute () {
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
                        Toast.makeText(TravelSummaryActivity.this, message, Toast.LENGTH_SHORT).show();

                    }
                });

    }
}
