package com.example.carbotrackphoneapplication;

// Here is the Java code for the travel process page
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.osmdroid.api.IMapController;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.List;

public class TravelProcessActivity extends AppCompatActivity {

    private MapView mapView;
    private double destLat, destLon;
    private FusedLocationProviderClient locationClient;
    private Handler handler = new Handler();
    private Runnable locationUpdater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_process);

        mapView = findViewById(R.id.mapView);
        mapView.setMultiTouchControls(true);

        destLat = getIntent().getDoubleExtra("destLat", 0);
        destLon = getIntent().getDoubleExtra("destLon", 0);

        locationClient = LocationServices.getFusedLocationProviderClient(this);

        IMapController mapController = mapView.getController();
        mapController.setZoom(16.0);

        startRealTimeTracking();

        Button endTravelBtn = findViewById(R.id.endTravelBtn);
        endTravelBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, TravelCompletedActivity.class);
            intent.putExtras(getIntent());
            startActivity(intent);
        });
    }

    private void startRealTimeTracking() {
        locationUpdater = new Runnable() {
            @Override
            public void run() {
                if (ActivityCompat.checkSelfPermission(TravelProcessActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                locationClient.getLastLocation().addOnSuccessListener(location -> {
                    if (location != null)
                    {
                        updateMap(location);
                    }
                });
                handler.postDelayed(this, 2000);
            }
        };
        handler.post(locationUpdater);
    }

    private void updateMap(Location location) {
        mapView.getOverlays().clear();

        GeoPoint current = new GeoPoint(location.getLatitude(), location.getLongitude());
        GeoPoint end = new GeoPoint(destLat, destLon);

        Marker startMarker = new Marker(mapView);
        startMarker.setPosition(current);
        startMarker.setTitle("You");
        mapView.getOverlays().add(startMarker);

        Marker endMarker = new Marker(mapView);
        endMarker.setPosition(end);
        endMarker.setTitle("Destination");
        mapView.getOverlays().add(endMarker);

        Utils.fetchRoute(this, current, end,
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
                        Toast.makeText(TravelProcessActivity.this, message, Toast.LENGTH_SHORT).show();

                    }
                });

        mapView.getController().setCenter(current);
    }

    @Override
    protected void onPause () {
        super.onPause();
        handler.removeCallbacks(locationUpdater);
    }
}
