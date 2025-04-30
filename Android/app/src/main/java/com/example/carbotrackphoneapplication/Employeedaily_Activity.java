// Here is the java code for the employee homepage.
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
import java.util.Calendar;

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
        milesValueText = findViewById(R.id.milesUnitText);
        total_carbon_credits = findViewById(R.id.total_carbon_credits);
        monthDropdown = findViewById(R.id.monthDropdown);
        weekDropdown = findViewById(R.id.weekDropdown);

        String currentDay = new SimpleDateFormat("EEE, MMM d", Locale.US).format(new Date());
        dateTextView.setText(currentDay);

        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, currentMonth);
        monthDropdown.setAdapter(monthAdapter);
        monthDropdown.setText(currentMonth[0], false);
        monthDropdown.setEnabled(true);

        int currentWeek = getCurrentWeek();
        ArrayAdapter<String> weekAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, weeks);
        weekDropdown.setAdapter(weekAdapter);
        weekDropdown.setText(weeks[currentWeek - 1], false);
        weekDropdown.setEnabled(false);

        // Here we are setting up the graph
        setupGraph();

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

    private int getCurrentWeek() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.WEEK_OF_MONTH);
    }

    private void setupGraph() {
        ArrayList<Entry> entries = new ArrayList<>();

        // Here we are creating values corresponding to days between Sat to Fri
        for (int i = 0; i < 7; i++)
        {
            entries.add(new Entry(i, 0));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Miles / Carbon Credits");
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawValues(false);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.getDescription().setEnabled(false);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.invalidate();
    }

    private void loadDailyData() {

        int employeeId = getSharedPreferences("CarboPrefs", MODE_PRIVATE).getInt("user_id", -1);
        if (employeeId == -1) 
        {
            return;
        }

        String baseUrl = "https://softwareengineeringproject-production.up.railway.app/api";

        new Thread(() -> {
            try {
                // Here is where we are getting the carbon credits accumulated by the employee for the employee homepage
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

                // Here is where we are getting the total miles accumulated by the employee for the employee homepage.
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
                    milesValueText.setText(miles + " Miles");
                    total_carbon_credits.setText("Total Carbon Credits: " + credits);

                    updateGraph(miles, credits);
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void updateGraph(int miles, int credits) {
        ArrayList<Entry> entries = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        int[] day_mapping = {6, 0, 1, 2, 3, 4, 5};

        for (int i = 0; i < 7; i++)
        {
            if (i == day_mapping[dayOfWeek - 1])
            {
                entries.add(new Entry(i, miles + credits));
            }
            else
            {
                entries.add(new Entry(i, 0));
            }
        }

        LineDataSet dataSet = new LineDataSet(entries, "Miles + Carbon Credits");
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawValues(false);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.getDescription().setEnabled(false);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.invalidate();
    }
}
