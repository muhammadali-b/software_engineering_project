package com.example.carbotrackphoneapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.BuildConfig;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.security.PrivateKey;

public class InitialTravelActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private MapView mapView;
    private FusedLocationProviderClient locationClient;
    private GeoPoint userLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().setUserAgentValue("CarboTrack");
        setContentView(R.layout.activity_initial_travel);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Here we are making sure that when a user is on a page from clicking it on the navigation bar, that page stays highlighted.
        bottomNavigationView.setSelectedItemId(R.id.nav_travel);

        mapView = findViewById(R.id.mapView);
        TextView tvWhereTo = findViewById(R.id.tvWhereTo);
        locationClient = LocationServices.getFusedLocationProviderClient(this);

        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        requestLocation();

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home)
            {
                startActivity(new Intent(InitialTravelActivity.this, Employeedaily_Activity.class));
                return true;
            }
            else if(id == R.id.nav_travel)
            {
                startActivity(new Intent(InitialTravelActivity.this, InitialTravelActivity.class));
                return true;

            }
            //Need to implement the profile page for the nav bar
//                case R.id.nav_profile:
//                    // startActivity(new Intent(...));
//                    return true;
            return false;
        });

        tvWhereTo.setOnClickListener(v -> {
            if (userLocation != null)
            {
                Intent intent = new Intent(this, PlanYourRideActivity.class);
                intent.putExtra("userLat", userLocation.getLatitude());
                intent.putExtra("userLon", userLocation.getLongitude());
                startActivity(intent);
            }
            else
            {
                tvWhereTo.setText("Detecting where your location is.");
            }
        });
    }

    private void requestLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        locationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null)
            {
                showCurrentLocationOnMap(location);
            }
        });
    }

    private void showCurrentLocationOnMap(Location location) {
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        userLocation = new GeoPoint(lat, lon);

        IMapController mapController = mapView.getController();
        mapController.setZoom(15.0);
        mapController.setCenter(userLocation);

        Marker marker = new Marker(mapView);
        marker.setPosition(userLocation);
        marker.setTitle("Your Location");
        mapView.getOverlays().add(marker);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permission, int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            requestLocation();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }


}
