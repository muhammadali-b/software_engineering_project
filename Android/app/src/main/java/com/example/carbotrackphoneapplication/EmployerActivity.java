package com.example.carbotrackphoneapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class EmployerActivity extends AppCompatActivity {

    LinearLayout employeeapprovalBtn, paymentBtn, tradingBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_activity); // ✅ Make sure your layout file name matches this!

        // ✅ Reference buttons from the XML layout (ensure these IDs exist in the XML)
        employeeapprovalBtn = findViewById(R.id.employeeapprovalBtn);
        paymentBtn = findViewById(R.id.paymentBtn);
        tradingBtn = findViewById(R.id.tradingBtn);

        // ✅ Bottom Navigation setup
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_activity); // Highlight current tab

        // ✅ Click listeners for card buttons
        employeeapprovalBtn.setOnClickListener(v -> {
            startActivity(new Intent(EmployerActivity.this, EmployeeApprovalActivity.class));
        });

        paymentBtn.setOnClickListener(v -> {
            startActivity(new Intent(EmployerActivity.this, EmployerPaymentActivity.class));
        });

        tradingBtn.setOnClickListener(v -> {
            startActivity(new Intent(EmployerActivity.this, EmployerTradingActivity.class));
        });

        // ✅ Bottom navigation listener
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                startActivity(new Intent(EmployerActivity.this, EmployerHomeActivity.class));
                return true;
            } else if (id == R.id.nav_activity) {
                // Stay on this page
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(EmployerActivity.this, EmployerProfileActivity.class));
                return true;
            }
            return false;
        });
    }
}
