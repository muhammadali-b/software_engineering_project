package com.example.carbotrackphoneapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class EmployerHomeActivity extends AppCompatActivity {

    Spinner employeeSpinner;
    TextView employeeName, employeeEmail, employeeMiles, employeeCredits;
    CircleImageView employeeImage;
    Button buyBtn, sellBtn;

    private Employee selectedEmployee = null;
    private final String BASE_URL = "https://softwareengineeringproject-production.up.railway.app/api";
    private String transactionType = "buy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_home);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        employeeSpinner = findViewById(R.id.employeeSpinner);
        employeeName = findViewById(R.id.employeeName);
        employeeEmail = findViewById(R.id.employeeEmail);
        employeeMiles = findViewById(R.id.employeeMiles);
        employeeCredits = findViewById(R.id.employeeCredits);
        employeeImage = findViewById(R.id.employeeImage);
        buyBtn = findViewById(R.id.buyBtn);
        sellBtn = findViewById(R.id.sellBtn);

        fetchApprovedEmployees();

        buyBtn.setOnClickListener(v -> {
            if (selectedEmployee != null) {
                transactionType = "buy";
                openBuySellModal();
            } else {
                Toast.makeText(this, "Select an employee first", Toast.LENGTH_SHORT).show();
            }
        });

        sellBtn.setOnClickListener(v -> {
            if (selectedEmployee != null) {
                transactionType = "sell";
                openBuySellModal();
            } else {
                Toast.makeText(this, "Select an employee first", Toast.LENGTH_SHORT).show();
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_activity) {
                startActivity(new Intent(this, EmployerActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, EmployerProfileActivity.class));
                return true;
            }
            return false;
        });
    }

    private void fetchApprovedEmployees() {
        new Thread(() -> {
            try {
                URL url = new URL(BASE_URL + "/approved-employees");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) response.append(line);
                reader.close();

                JSONObject json = new JSONObject(response.toString());
                JSONArray data = json.getJSONArray("data");

                ArrayList<String> employeeNames = new ArrayList<>();
                ArrayList<Employee> employeeList = new ArrayList<>();

                for (int i = 0; i < data.length(); i++) {
                    JSONObject obj = data.getJSONObject(i);
                    Employee emp = new Employee(
                            obj.getInt("id"),
                            obj.getString("f_name"),
                            obj.getString("l_name"),
                            obj.getString("email")
                    );
                    employeeList.add(emp);
                    employeeNames.add(emp.f_name + " " + emp.l_name);
                }

                runOnUiThread(() -> {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, employeeNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    employeeSpinner.setAdapter(adapter);

                    if (!employeeList.isEmpty()) {
                        selectedEmployee = employeeList.get(0);
                        updateEmployeeCard(selectedEmployee);
                        fetchStatsForEmployee(selectedEmployee.id);
                    }

                    employeeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            selectedEmployee = employeeList.get(position);
                            updateEmployeeCard(selectedEmployee);
                            fetchStatsForEmployee(selectedEmployee.id);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void updateEmployeeCard(Employee emp) {
        employeeName.setText(emp.f_name + " " + emp.l_name);
        employeeEmail.setText(emp.email);
        employeeMiles.setText("Loading...");
        employeeCredits.setText("Loading...");
        employeeImage.setImageResource(R.drawable.person_24);
    }

    private void fetchStatsForEmployee(int employeeId) {
        new Thread(() -> {
            int miles = 0, credits = 0;
            try {
                // Fetch miles
                URL milesURL = new URL(BASE_URL + "/latest-miles/" + employeeId);
                HttpURLConnection connMiles = (HttpURLConnection) milesURL.openConnection();
                connMiles.setRequestMethod("GET");

                BufferedReader readerMiles = new BufferedReader(new InputStreamReader(connMiles.getInputStream()));
                StringBuilder milesResp = new StringBuilder();
                String line;
                while ((line = readerMiles.readLine()) != null) milesResp.append(line);
                readerMiles.close();

                JSONObject milesJSON = new JSONObject(milesResp.toString());
                if (milesJSON.has("data")) {
                    miles = milesJSON.getJSONObject("data").optInt("miles", 0);
                }

                // Fetch credits
                URL creditsURL = new URL(BASE_URL + "/latest-credits/" + employeeId);
                HttpURLConnection connCredits = (HttpURLConnection) creditsURL.openConnection();
                connCredits.setRequestMethod("GET");

                BufferedReader readerCredits = new BufferedReader(new InputStreamReader(connCredits.getInputStream()));
                StringBuilder creditsResp = new StringBuilder();
                while ((line = readerCredits.readLine()) != null) creditsResp.append(line);
                readerCredits.close();

                JSONObject creditsJSON = new JSONObject(creditsResp.toString());
                credits = creditsJSON.optInt("carbon_credits", 0);

            } catch (Exception e) {
                e.printStackTrace();
            }

            int finalMiles = miles;
            int finalCredits = credits;

            runOnUiThread(() -> {
                employeeMiles.setText(String.valueOf(finalMiles));
                employeeCredits.setText(String.valueOf(finalCredits));
            });

        }).start();
    }

    private void openBuySellModal() {
        View modalView = LayoutInflater.from(this).inflate(R.layout.dialog_buy_sell_modal, null);

        TextView modalTitle = modalView.findViewById(R.id.modalTitle);
        TextView dropdownLabel = modalView.findViewById(R.id.dropdownLabel);
        TextView quantityText = modalView.findViewById(R.id.quantityText);
        Spinner partnerSpinner = modalView.findViewById(R.id.partnerSpinner);
        ImageButton incrementBtn = modalView.findViewById(R.id.incrementBtn);
        ImageButton decrementBtn = modalView.findViewById(R.id.decrementBtn);
        Button cancelBtn = modalView.findViewById(R.id.cancelBtn);
        Button checkoutBtn = modalView.findViewById(R.id.checkoutBtn);

        modalTitle.setText(transactionType.equals("buy") ? "Buy Carbon Credits" : "Sell Carbon Credits");
        dropdownLabel.setText(transactionType.equals("buy") ? "From" : "To");

        int[] creditQuantity = {15}; // Mutable wrapper to use in inner class
        quantityText.setText(String.valueOf(creditQuantity[0]));

        String[] partners = {"ABC", "XYZ", "SOS", "LOL", "SUM", "SUB", "MUL", "DIV", "BTS"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, partners);
        partnerSpinner.setAdapter(adapter);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(modalView)
                .setCancelable(true)
                .create();

        incrementBtn.setOnClickListener(v -> {
            creditQuantity[0]++;
            quantityText.setText(String.valueOf(creditQuantity[0]));
        });

        decrementBtn.setOnClickListener(v -> {
            if (creditQuantity[0] > 1) {
                creditQuantity[0]--;
                quantityText.setText(String.valueOf(creditQuantity[0]));
            }
        });

        cancelBtn.setOnClickListener(v -> dialog.dismiss());

        checkoutBtn.setOnClickListener(v -> {
            int amount = creditQuantity[0];
            String url = BASE_URL + "/" + (transactionType.equals("buy") ? "buy-credits" : "sell-credits");

            new Thread(() -> {
                try {
                    JSONObject json = new JSONObject();
                    json.put("employee_id", selectedEmployee.id);
                    json.put("amount", amount);

                    URL endpoint = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection) endpoint.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    conn.setDoOutput(true);

                    OutputStream os = conn.getOutputStream();
                    os.write(json.toString().getBytes("UTF-8"));
                    os.close();

                    int responseCode = conn.getResponseCode();
                    conn.disconnect();

                    runOnUiThread(() -> {
                        if (responseCode == 200 || responseCode == 201) {
                            Toast.makeText(this, "Transaction successful", Toast.LENGTH_SHORT).show();
                            fetchStatsForEmployee(selectedEmployee.id);
                        } else {
                            Toast.makeText(this, "Transaction failed", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    });

                } catch (Exception e) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    });
                }
            }).start();
        });

        dialog.show();
    }

    private static class Employee {
        int id;
        String f_name;
        String l_name;
        String email;

        Employee(int id, String f_name, String l_name, String email) {
            this.id = id;
            this.f_name = f_name;
            this.l_name = l_name;
            this.email = email;
        }
    }
}
