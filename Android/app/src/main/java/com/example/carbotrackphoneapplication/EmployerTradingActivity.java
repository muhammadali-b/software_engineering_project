package com.example.carbotrackphoneapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class EmployerTradingActivity extends AppCompatActivity {

    private LinearLayout tradeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_trading);

        tradeContainer = findViewById(R.id.tradeContainer);
        ImageView backArrow = findViewById(R.id.back_button);
        backArrow.setOnClickListener(v -> finish());

        loadTradeCards();
    }

    private void loadTradeCards() {
        String[][] trades = {
                {"ABCXYZ", "abcxyz@gmail.com", "Mar 30 · 2:00 PM", "120cc", "$5/1cc", "$100"},
                {"ABCXYZ", "abcxyz@gmail.com", "Mar 30 · 2:00 PM", "120cc", "$5/1cc", "$100"}
        };

        for (String[] trade : trades) {
            View card = LayoutInflater.from(this).inflate(R.layout.item_trade_card, tradeContainer, false);

            ((TextView) card.findViewById(R.id.name)).setText(trade[0]);
            ((TextView) card.findViewById(R.id.email)).setText(trade[1]);
            ((TextView) card.findViewById(R.id.time)).setText(trade[2]);
            ((TextView) card.findViewById(R.id.cc)).setText(trade[3]);
            ((TextView) card.findViewById(R.id.rate)).setText(trade[4]);
            ((TextView) card.findViewById(R.id.amount)).setText(trade[5]);

            tradeContainer.addView(card);
        }
    }
}
