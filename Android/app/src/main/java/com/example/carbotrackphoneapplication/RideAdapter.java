package com.example.carbotrackphoneapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
public class RideAdapter extends RecyclerView.Adapter<RideAdapter.ViewHolder> {
    private final List<RideOption> rideList;
    private final OnRideClickListener listener;

    public interface OnRideClickListener {
        void onRideClick(RideOption ride);
    }

    public RideAdapter(List<RideOption> rideList, OnRideClickListener listener) {
        this.rideList = rideList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ride_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RideOption ride = rideList.get(position);
        holder.rideType.setText(ride.getType());
        holder.rideTime.setText(ride.getTime());
        holder.rideCost.setText(ride.getCost());

        holder.itemView.setOnClickListener(v -> listener.onRideClick(ride));

    }

    @Override
    public int getItemCount() {
        return rideList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView rideType, rideTime, rideCost;
        public ViewHolder(View itemView) {
            super(itemView);
            rideType = itemView.findViewById(R.id.rideType);
            rideTime = itemView.findViewById(R.id.rideTime);
            rideCost = itemView.findViewById(R.id.rideCost);
        }
    }

}
