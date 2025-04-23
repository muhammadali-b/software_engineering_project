package com.example.carbotrackphoneapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class EmployeeApprovalActivity extends AppCompatActivity {

    RecyclerView approvalRecyclerView;
    ArrayList<Employee> employeeList;
    ApprovalAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_approval);

        ImageView backButton = findViewById(R.id.back_button);
        approvalRecyclerView = findViewById(R.id.approvalRecyclerView);

        backButton.setOnClickListener(view -> finish());

        // Dummy data
        employeeList = new ArrayList<>();
        employeeList.add(new Employee("ABCXYZ", "abczyx@gmail.com", "Mar 30 · 2:00 PM"));
        employeeList.add(new Employee("Jane Doe", "jane@example.com", "Apr 3 · 10:30 AM"));
        employeeList.add(new Employee("John Smith", "johnsmith@mail.com", "Apr 6 · 1:45 PM"));

        adapter = new ApprovalAdapter(employeeList);
        approvalRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        approvalRecyclerView.setAdapter(adapter);
    }

    // Model
    static class Employee {
        String name, email, time;

        Employee(String name, String email, String time) {
            this.name = name;
            this.email = email;
            this.time = time;
        }
    }
}
