// This is the java code for the employee approvals page.
package com.example.carbotrackphoneapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class EmployeeApprovalActivity extends AppCompatActivity {

    RecyclerView approvalRecyclerView;
    ApprovalAdapter adapter;
    ArrayList<Employee> employeeList = new ArrayList<>();
    String BASE_URL = "https://softwareengineeringproject-production.up.railway.app/api";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_approval);

        ImageView backButton = findViewById(R.id.back_button);
        approvalRecyclerView = findViewById(R.id.approvalRecyclerView);

        backButton.setOnClickListener(view -> finish());

        adapter = new ApprovalAdapter(employeeList);
        approvalRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        approvalRecyclerView.setAdapter(adapter);

        adapter.setOnApproveListener(this::approveEmployee);
        fetchUnapprovedEmployees();
    }

    private void fetchUnapprovedEmployees() {
        new Thread(() -> {
            try {
                URL url = new URL(BASE_URL + "/unapproved-employees");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) response.append(line);
                reader.close();

                JSONObject json = new JSONObject(response.toString());
                JSONArray employees = json.getJSONArray("employees");

                employeeList.clear();
                for (int i = 0; i < employees.length(); i++) {
                    JSONObject obj = employees.getJSONObject(i);
                    employeeList.add(new Employee(
                            obj.getInt("id"),
                            obj.getString("f_name") + " " + obj.getString("l_name"),
                            obj.getString("email"),
                            "" // no date/time for now
                    ));
                }

                runOnUiThread(() -> adapter.notifyDataSetChanged());

            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Failed to fetch employees", Toast.LENGTH_SHORT).show());
                e.printStackTrace();
            }
        }).start();
    }

    private void approveEmployee(int userId, int position) {
        new Thread(() -> {
            try {
                URL url = new URL(BASE_URL + "/approve-employee/" + userId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("PATCH");
                conn.getInputStream().close();

                runOnUiThread(() -> {
                    Toast.makeText(this, "Employee approved", Toast.LENGTH_SHORT).show();
                    adapter.removeAt(position);
                });

            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Approval failed", Toast.LENGTH_SHORT).show());
                e.printStackTrace();
            }
        }).start();
    }

    public static class Employee {
        int id;
        String name, email, time;

        public Employee(int id, String name, String email, String time) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.time = time;
        }
    }
}
