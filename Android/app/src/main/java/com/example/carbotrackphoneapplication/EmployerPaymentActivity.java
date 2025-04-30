// This is the java code written to display the transaction history of the employer.
package com.example.carbotrackphoneapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class EmployerPaymentActivity extends AppCompatActivity {

    LinearLayout paymentHistoryContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_payment_activity);

        ImageView backArrow = findViewById(R.id.back_button);
        paymentHistoryContainer = findViewById(R.id.paymentHistoryContainer);

        backArrow.setOnClickListener(view -> finish());

        loadAllTransactionHistory();
    }

    private void loadAllTransactionHistory() {
        new Thread(() -> {
            try {
                URL url = new URL("https://softwareengineeringproject-production.up.railway.app/api/all-transactions");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) response.append(line);
                reader.close();

                JSONObject json = new JSONObject(response.toString());
                JSONArray data = json.getJSONArray("data");

                runOnUiThread(() -> {
                    paymentHistoryContainer.removeAllViews();

                    for (int i = 0; i < data.length(); i++) {
                        try {
                            JSONObject item = data.getJSONObject(i);

                            int employeeId = item.getInt("employee_id");
                            String type = item.getString("type");
                            int amount = item.getInt("amount");
                            String date = item.getString("transaction_date");

                            View paymentItem = LayoutInflater.from(this).inflate(R.layout.item_payment_card, null);

                            // Hide rate and amount
                            paymentItem.findViewById(R.id.rate).setVisibility(View.GONE);
                            paymentItem.findViewById(R.id.amount).setVisibility(View.GONE);

                            ((TextView) paymentItem.findViewById(R.id.status)).setText("Success");
                            ((TextView) paymentItem.findViewById(R.id.cc)).setText(amount + "cc");
                            ((TextView) paymentItem.findViewById(R.id.time)).setText(date);
                            ((TextView) paymentItem.findViewById(R.id.email)).setText("Loading...");

                            paymentHistoryContainer.addView(paymentItem);

                            // Fetch name for each user separately
                            fetchAndSetUserName(employeeId, paymentItem);

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Failed to load transactions", Toast.LENGTH_SHORT).show());
                e.printStackTrace();
            }
        }).start();
    }

    private void fetchAndSetUserName(int userId, View view) {
        new Thread(() -> {
            try {
                URL url = new URL("https://softwareengineeringproject-production.up.railway.app/api/user/" + userId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) response.append(line);
                reader.close();

                JSONObject json = new JSONObject(response.toString());
                JSONObject data = json.getJSONObject("data");

                String fName = data.optString("f_name", "");
                String lName = data.optString("l_name", "");
                String fullName = (fName + " " + lName).trim();

                runOnUiThread(() -> {
                    ((TextView) view.findViewById(R.id.email)).setText(fullName);
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    ((TextView) view.findViewById(R.id.email)).setText("Unknown");
                });
                e.printStackTrace();
            }
        }).start();
    }
}
