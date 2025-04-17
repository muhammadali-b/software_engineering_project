package com.example.carbotrackphoneapplication;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Employeedaily_Activity extends AppCompatActivity {

    TextView dateTextView, milesValueText, total_carbon_credits;
    Button dailyButton;
    LineChart lineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_daily);

        // Bind views
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        lineChart = findViewById(R.id.lineChart);
        dateTextView = findViewById(R.id.dateTextView);
        milesValueText = findViewById(R.id.milesValueText);
        total_carbon_credits = findViewById(R.id.total_carbon_credits);

        // Load initial daily data
        loadDailyData();
        daily_graph();


        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
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
            //Need to implement the profile page for the nav bar
//                case R.id.nav_profile:
//                    // startActivity(new Intent(...));
//                    return true;
            return false;
        });

    }

    private void loadDailyData() {
        String currentDay = new SimpleDateFormat("EEE, MMM d", Locale.US).format(new Date());
        dateTextView.setText(currentDay);
        milesValueText.setText("2500");
        total_carbon_credits.setText("Total Carbon Credits: 2500");
    }

    private void daily_graph() {
        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, 200));
        entries.add(new Entry(1, 300));
        entries.add(new Entry(2, 400));
        entries.add(new Entry(3, 500));
        entries.add(new Entry(4, 600));
        entries.add(new Entry(5, 700));
        entries.add(new Entry(6, 800));

        LineDataSet dataSet = new LineDataSet(entries, "Miles / Carbon Credits");
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawValues(false);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.getDescription().setEnabled(false);
        lineChart.invalidate();
    }

}
