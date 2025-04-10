package com.example.carbotrackphoneapplication;
// Here is the Java code for the Employee homepage.

// Needs to be updated when we can receive data
import android.os.Bundle;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;
import android.text.Html;
import androidx.appcompat.app.AppCompatActivity;

// These will be for displaying the actual date to the Employee.
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import com.google.android.material.bottomnavigation.BottomNavigationView;

// These will be responsible for generating the line graph of the carbon credits/miles earned by the Employee.
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;


public class EmployeedailyActivity extends AppCompatActivity {

    TextView dateTextView, milesValueText, total_carbon_credits;
    Button dailyButton, monthlyButton;
    LineChart lineChart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_daily);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Here is the functionality of the navigation bar when the employee clicks on any of the options.
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch(item.getItemId())
            {
                case R.id.nav_home:
                    startActivity(new Intent(EmployeedailyActivity.this, EmployeedailyActivity.class));
                    break;
                // Need to finish this when the travel/profile pages are done
                case R.id.nav_travel:
                    startActivity(new Intent(EmployeedailyActivity.this,  ));
                    break;
                case R.id.nav_profile:
                    startActivity(new Intent(EmployeedailyActivity.this, ));
                    break;

            }
            return true;

            //Here we are initially showing the daily line graph view
            lineChart = findViewById(R.id.lineChart);
            daily_graph();

        });

        // Here is the functionality when the employee presses on the monthly button to see their data they accumulated monthly
        dateTextView = findViewById(R.id.dateTextView);
        milesValueText = findViewById(R.id.milesValueText);
        total_carbon_credits = findViewById(R.id.total_carbon_credits);

        dailyButton = findViewById(R.id.daily_button);
        monthlyButton = findViewById(R.id.monthly_button);

        loadDailyData();

        dailyButton.setOnClickListener(v -> {
            loadDailyData();
            daily_graph();
        });

        monthlyButton.setOnClickListener(v -> {
            loadMonthlyData();
            monthly_graph();
        });

    }

    private void loadDailyData ()
    {
        // Here we are getting the current day.
        String currentDay = new SimpleDateFormat("EEE, MMM d", Locale.US).format(new Date());

        dateTextView.setText(currentDay);

        // Need to update these for when the backend is done
        milesValueText.setText("2500");
        total_carbon_credits.setText("Total Carbon Credits: 2500");
    }

    private void loadMonthlyData()
    {
        //Here we are getting the current month.
        String currentMonth = new SimpleDateFormat("MMMM YYYY", Locale.US).format(new Date());

        dateTextView.setText(currentMonth);
        milesValueText.setText("3000");
        total_carbon_credits.setText("Total Carbon Credits: 3000");

    }

    // This will be responsible for showing the daily graph to the employee
    private void daily_graph()
    {
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
        dataSet.setDrawValues(fales);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.getDescription().setEnabled(false);
        lineChart.invalidate();


    }

    // This will be responsible for showing the monthly graph to the employee
    private void monthly_graph()
    {
        ArrayList<Entry> entries = new ArrayList<>();

        entries.add(new Entry(0, 1200));
        entries.add(new Entry(1, 1300));
        entries.add(new Entry(2, 1400));
        entries.add(new Entry(3, 1500));
        entries.add(new Entry(4, 1600));
        entries.add(new Entry(5, 1700));
        entries.add(new Entry(6, 1800));

        LineDataSet dataSet = new LineDataSet(entries, "Miles / Carbon Credits");
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawValues(fales);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.getDescription().setEnabled(false);
        lineChart.invalidate();
    }

}

