package com.example.carbotrackphoneapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ApprovalAdapter extends RecyclerView.Adapter<ApprovalAdapter.ViewHolder> {

    List<EmployeeApprovalActivity.Employee> employeeList;
    private OnApproveListener approveListener;

    public ApprovalAdapter(List<EmployeeApprovalActivity.Employee> employeeList) {
        this.employeeList = employeeList;
    }

    public void setOnApproveListener(OnApproveListener listener) {
        this.approveListener = listener;
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
        holder.time.setVisibility(View.GONE); // hide time
        holder.denyBtn.setVisibility(View.GONE); // hide deny

        holder.approveBtn.setOnClickListener(v -> {
            if (approveListener != null) {
                approveListener.onApprove(employee.id, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }

    public void removeAt(int position) {
        employeeList.remove(position);
        notifyItemRemoved(position);
    }

    public interface OnApproveListener {
        void onApprove(int employeeId, int position);
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
