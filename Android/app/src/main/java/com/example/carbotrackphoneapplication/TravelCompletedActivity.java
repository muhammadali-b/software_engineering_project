package com.example.carbotrackphoneapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
public class TravelCompletedActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_completed);

        TextView creditsText = findViewById(R.id.creditsEarnedText);
        Button doneBtn = findViewById(R.id.doneBtn);

        int credits = getIntent().getIntExtra("credits", 0);
        creditsText.setText("Carbon Credits Earned: " + credits);

        doneBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, InitialTravelActivity.class);
            startActivity(intent);
        });
    }
}
