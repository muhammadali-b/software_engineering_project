package com.example.carbotrackphoneapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

public class EmployerPaymentActivity extends AppCompatActivity {

    LinearLayout paymentHistoryContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_payment_activity);

        ImageView backArrow = findViewById(R.id.back_button);
        Button sellBtn = findViewById(R.id.sellBtn);
        Button purchaseBtn = findViewById(R.id.purchaseBtn);
        paymentHistoryContainer = findViewById(R.id.paymentHistoryContainer);

        // Back button
        backArrow.setOnClickListener(view -> finish());

        // Load dummy payments
        loadPaymentData();
    }

    private void loadPaymentData() {
        String[][] paymentData = {
                {"abczyx@gmail.com", "Mar 30 - 2:00 PM", "Pending", "120cc", "$5/1cc", "$100"},
                {"abczyx@gmail.com", "Mar 30 - 2:00 PM", "Approved", "120cc", "$5/1cc", "$100"}
        };

        for (String[] data : paymentData) {
            View paymentItem = getLayoutInflater().inflate(R.layout.item_payment_card, null);

            ((TextView) paymentItem.findViewById(R.id.email)).setText(data[0]);
            ((TextView) paymentItem.findViewById(R.id.time)).setText(data[1]);
            ((TextView) paymentItem.findViewById(R.id.status)).setText(data[2]);
            ((TextView) paymentItem.findViewById(R.id.cc)).setText(data[3]);
            ((TextView) paymentItem.findViewById(R.id.rate)).setText(data[4]);
            ((TextView) paymentItem.findViewById(R.id.amount)).setText(data[5]);

            paymentHistoryContainer.addView(paymentItem);
        }
    }
}
