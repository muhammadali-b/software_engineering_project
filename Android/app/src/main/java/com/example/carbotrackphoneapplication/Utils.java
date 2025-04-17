package com.example.carbotrackphoneapplication;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    // This is the api key that uses the oepn route service api for map routing
    private static final String API_KEY = "5b3ce3597851110001cf6248ffe49893580a461e95f60d43ed5b56b6";

    public interface RouteCallback {
        void onRouteReady(List<GeoPoint> routePoints);
        void onError(String message);
    }

    public static float calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        float[] results = new float[1];
        android.location.Location.distanceBetween(lat1, lon1, lat2, lon2, results);
        return results[0] / 1609f; // Here we are converting meters to miles
    }

    public static int calculateCarbonCredits(float distance, String transportMode) {
        switch(transportMode.toLowerCase()) {
            case "bike":
                return (int) (distance * 3);
            case "public transport":
                return (int) (distance * 2);
            case "rideshare":
                return (int) (distance * 1.5);
            case "car":
                return (int) (distance * 1);
            default:
                return (int) distance;
        }
    }

    public static void fetchRoute(Context context, GeoPoint start, GeoPoint end, RouteCallback callback) {
        String url = "https://api.openrouteservice.org/v2/directions/driving-car?api_key=" + API_KEY +
                "&start=" + start.getLongitude() + "," + start.getLatitude() +
                "&end=" + end.getLongitude() + "," + end.getLatitude();

        RequestQueue queue = Volley.newRequestQueue(context);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,

                response -> {
                    try {
                        JSONArray coordinates = response
                                .getJSONArray("features")
                                .getJSONObject(0)
                                .getJSONObject("geometry")
                                .getJSONArray("coordinates");

                        List<GeoPoint> routePoints = new ArrayList<>();
                        for (int i = 0; i < coordinates.length(); i++)
                        {
                            JSONArray coord = coordinates.getJSONArray(i);
                            double lon = coord.getDouble(0);
                            double lat = coord.getDouble(1);
                            routePoints.add(new GeoPoint(lat, lon));
                        }
                        callback.onRouteReady(routePoints);
                    }
                    catch (Exception e)
                    {
                        callback.onError("Parsing error");
                    }

                },
                error -> callback.onError("Network Error: " + error.getMessage())
        );
        queue.add(request);
    }
}
