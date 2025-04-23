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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Employeedaily_Activity extends AppCompatActivity {

    TextView dateTextView, milesValueText, total_carbon_credits;
    AutoCompleteTextView monthDropdown, weekDropdown;
    LineChart lineChart;

    // Here is where we are creating arrays of strings that will hold the current month and week.
    String[] currentMonth = {new SimpleDateFormat("MMMM", Locale.US).format(new Date())};
    String[] weeks = {"Week 1", "Week 2", "Week 3", "Week 4", "Week 5"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_daily);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Here we are making sure that when a user is on a page from clicking it on the navigation bar, that page stays highlighted.
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        // Here is where we are initializing the views for the page.
        lineChart = findViewById(R.id.lineChart);
        dateTextView = findViewById(R.id.dateTextView);
        milesValueText = findViewById(R.id.milesValueText);
        total_carbon_credits = findViewById(R.id.total_carbon_credits);
        monthDropdown = findViewById(R.id.monthDropdown);
        weekDropdown = findViewById(R.id.weekDropdown);

        // Here is where we are adding the functionalities for the month dropdown button.
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, currentMonth);
        monthDropdown.setAdapter(monthAdapter);
        monthDropdown.setText(currentMonth[0], false);
        monthDropdown.setEnabled(true);  //Here users are able to select between the different months

        // Here is where we are adding functionalities for the week dropdown
        ArrayAdapter<String> weekAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, weeks);
        weekDropdown.setAdapter(weekAdapter);
        weekDropdown.setOnItemClickListener((parent, view, position, id) -> {
            // Here add logic to use an api endpoint to get data about the employee's accumulated carbon credits and miles.
            weekly_graphData(position + 1);
        });

        // Load initial daily data
        loadDailyData();


        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home)
            {
                startActivity(new Intent(Employeedaily_Activity.this, Employeedaily_Activity.class));
                return true;
            }
            else if(id == R.id.nav_travel)
            {
                startActivity(new Intent(Employeedaily_Activity.this, InitialTravelActivity.class));
                return true;

            }
            else if (id == R.id.nav_profile)
            {
                startActivity(new Intent(Employeedaily_Activity.this, EmployeeProfileActivity.class));
                return true;
            }
            return false;
        });

    }

    private void loadDailyData() {
        String currentDay = new SimpleDateFormat("EEE, MMM d", Locale.US).format(new Date());
        dateTextView.setText(currentDay);

        // Need to change this to integrate the api endpoint that will be responsible for getting the carbon credits and the miles from the database.
        milesValueText.setText("2500");
        total_carbon_credits.setText("Total Carbon Credits: 2500");
    }


    private void weekly_graphData (int weekNumber)
    {
        // Need to implement logic here to use the api endpoint for getting data from the database to display on the line graph.
        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, 100 * weekNumber));
        entries.add(new Entry(1, 50 * weekNumber));
        entries.add(new Entry(2, 200 * weekNumber));
        entries.add(new Entry(3, 20 * weekNumber));
        entries.add(new Entry(4, 300 * weekNumber));
        entries.add(new Entry(5, 30 * weekNumber));
        entries.add(new Entry(6, 400 * weekNumber));

        // Need to update this to
        LineDataSet dataSet = new LineDataSet(entries, "Miles /  Carbon Credits (Week " + weekNumber + ")");
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawValues(false);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.getDescription().setEnabled(false);
        lineChart.invalidate();

    }

}
