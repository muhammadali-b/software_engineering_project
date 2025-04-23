package com.example.carbotrackphoneapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ApprovalAdapter extends RecyclerView.Adapter<ApprovalAdapter.ViewHolder> {

    List<EmployeeApprovalActivity.Employee> employeeList;

    public ApprovalAdapter(List<EmployeeApprovalActivity.Employee> employeeList) {
        this.employeeList = employeeList;
    }

    @NonNull
    @Override
    public ApprovalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_employee_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApprovalAdapter.ViewHolder holder, int position) {
        EmployeeApprovalActivity.Employee employee = employeeList.get(position);
        holder.name.setText(employee.name);
        holder.email.setText(employee.email);
        holder.time.setText(employee.time);
    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, email, time;
        Button approveBtn, denyBtn;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            email = itemView.findViewById(R.id.email);
            time = itemView.findViewById(R.id.time);
            approveBtn = itemView.findViewById(R.id.approveBtn);
            denyBtn = itemView.findViewById(R.id.denyBtn);
        }
    }
}
