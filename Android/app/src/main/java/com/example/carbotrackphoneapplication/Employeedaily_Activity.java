package com.example.carbotrackphoneapplication;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.os.Handler;
import android.os.Looper;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.URL;

public class Employeedaily_Activity extends AppCompatActivity {

    TextView dateTextView, milesValueText, total_carbon_credits;
    AutoCompleteTextView monthDropdown, weekDropdown;
    LineChart lineChart;

    String[] currentMonth = {new SimpleDateFormat("MMMM", Locale.US).format(new Date())};
    String[] weeks = {"Week 1", "Week 2", "Week 3", "Week 4", "Week 5"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_daily);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        lineChart = findViewById(R.id.lineChart);
        dateTextView = findViewById(R.id.dateTextView);
        milesValueText = findViewById(R.id.milesValueText);
        total_carbon_credits = findViewById(R.id.total_carbon_credits);
        monthDropdown = findViewById(R.id.monthDropdown);
        weekDropdown = findViewById(R.id.weekDropdown);

        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, currentMonth);
        monthDropdown.setAdapter(monthAdapter);
        monthDropdown.setText(currentMonth[0], false);
        monthDropdown.setEnabled(true);

        ArrayAdapter<String> weekAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, weeks);
        weekDropdown.setAdapter(weekAdapter);
        weekDropdown.setOnItemClickListener((parent, view, position, id) -> {
            weekly_graphData(position + 1);
        });

        loadDailyData();

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, Employeedaily_Activity.class));
                return true;
            } else if (id == R.id.nav_travel) {
                startActivity(new Intent(this, InitialTravelActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, EmployeeProfileActivity.class));
                return true;
            }
            return false;
        });
    }

    private void loadDailyData() {
        String currentDay = new SimpleDateFormat("EEE, MMM d", Locale.US).format(new Date());
        dateTextView.setText(currentDay);

        int employeeId = getSharedPreferences("CarboPrefs", MODE_PRIVATE).getInt("user_id", -1);
        if (employeeId == -1) return;

        String baseUrl = "https://softwareengineeringproject-production.up.railway.app/api";

        new Thread(() -> {
            try {
                // Fetch carbon credits
                URL urlCredits = new URL(baseUrl + "/latest-credits/" + employeeId);
                HttpURLConnection connCredits = (HttpURLConnection) urlCredits.openConnection();
                connCredits.setRequestMethod("GET");

                BufferedReader readerCredits = new BufferedReader(new InputStreamReader(connCredits.getInputStream()));
                StringBuilder responseCredits = new StringBuilder();
                String line;
                while ((line = readerCredits.readLine()) != null) responseCredits.append(line);
                readerCredits.close();

                JSONObject jsonCredits = new JSONObject(responseCredits.toString());
                int credits = jsonCredits.optInt("carbon_credits", 0);

                // Fetch miles
                URL urlMiles = new URL(baseUrl + "/latest-miles/" + employeeId);
                HttpURLConnection connMiles = (HttpURLConnection) urlMiles.openConnection();
                connMiles.setRequestMethod("GET");

                BufferedReader readerMiles = new BufferedReader(new InputStreamReader(connMiles.getInputStream()));
                StringBuilder responseMiles = new StringBuilder();
                while ((line = readerMiles.readLine()) != null) responseMiles.append(line);
                readerMiles.close();

                JSONObject jsonMiles = new JSONObject(responseMiles.toString());
                JSONObject dataObject = jsonMiles.getJSONObject("data");
                int miles = dataObject.optInt("miles", 0);

                new Handler(Looper.getMainLooper()).post(() -> {
                    int m = miles;
                    int cc = credits;
                    milesValueText.setText(String.valueOf(miles));
                    total_carbon_credits.setText("Total Carbon Credits: " + credits);
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void weekly_graphData(int weekNumber) {
        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, 100 * weekNumber));
        entries.add(new Entry(1, 50 * weekNumber));
        entries.add(new Entry(2, 200 * weekNumber));
        entries.add(new Entry(3, 20 * weekNumber));
        entries.add(new Entry(4, 300 * weekNumber));
        entries.add(new Entry(5, 30 * weekNumber));
        entries.add(new Entry(6, 400 * weekNumber));

        LineDataSet dataSet = new LineDataSet(entries, "Miles / Carbon Credits (Week " + weekNumber + ")");
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawValues(false);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.getDescription().setEnabled(false);
        lineChart.invalidate();
    }
}
