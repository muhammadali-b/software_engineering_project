package com.example.carbotrackphoneapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;



public class TravelCompletedActivity extends AppCompatActivity {

    String apiURL = "https://softwareengineeringproject-production.up.railway.app/api/add-trip";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_travel_completed);

        TextView creditsText = findViewById(R.id.creditsEarnedText);
        Button doneBtn = findViewById(R.id.doneBtn);

        int credits = getIntent().getIntExtra("credits", 0);
        creditsText.setText("Carbon Credits Earned: " + credits);

        doneBtn.setOnClickListener(v -> {

            new Thread(() -> {
                try {

                    // Here is where we are getting the employee id after they log into the app.
                    int employee_id = getSharedPreferences("CarboTrackPrefs", MODE_PRIVATE).getInt("employee_id", -1);
                    String vehicle_type = getIntent().getStringExtra("rideType");
                    float distance = getIntent().getFloatExtra("distance", 0);

                    // Here is a check to make sure that the employee id was taken after login
                    if (employee_id == -1)
                    {
                        runOnUiThread(() -> Toast.makeText(this, "Error: Employee ID was not found.", Toast.LENGTH_SHORT).show());
                        return;
                    }

                    URL url = new URL(apiURL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    conn.setDoOutput(true);

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("employee_id", employee_id);
                    jsonParam.put("vehicle_type", vehicle_type.toLowerCase().replace(" ", "_"));
                    jsonParam.put("miles", String.format("%.2f", distance));

                    OutputStream os = conn.getOutputStream();
                    os.write(jsonParam.toString().getBytes("UTF-8"));
                    os.close();

                    int code = conn.getResponseCode();

                    // Here is an if/else condition that is checking to make sure that the trip data is saved if the response code is a 200 or 201
                    if (code == 200 || code == 201)
                    {
                        runOnUiThread(() -> Toast.makeText(this, "Your trip was saved successfully!", Toast.LENGTH_SHORT).show());
                    }
                    else
                    {
                        runOnUiThread(() -> Toast.makeText(this, "Your trip could not be saved.", Toast.LENGTH_SHORT).show());
                    }

                    conn.disconnect();

                } catch (Exception e) {
                    runOnUiThread(() -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }

                //Here we are going back to the initial travel page
                Intent intent = new Intent(this, InitialTravelActivity.class);
                startActivity(intent);
            }).start();

        });
    }
}
