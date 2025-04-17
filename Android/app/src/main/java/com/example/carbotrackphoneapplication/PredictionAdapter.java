package com.example.carbotrackphoneapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
public class PredictionAdapter extends RecyclerView.Adapter<PredictionAdapter.ViewHolder> {
    private List<Prediction> predictions;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick (String name, double lat, double lon);
    }

    public PredictionAdapter(List<Prediction> predictions, OnItemClickListener listener) {
        this.predictions = predictions;
        this.listener = listener;
    }

    public void updateData(List<Prediction> newPredictions) {
        this.predictions = newPredictions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_prediction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Prediction prediction = predictions.get(position);
        holder.name.setText(prediction.getName());
        holder.distance.setText(prediction.getDistance());

        holder.itemView.setOnClickListener(v -> listener.onItemClick(
           prediction.getName(),
           prediction.getLat(),
           prediction.getLon()
        ));
    }

    @Override
    public int getItemCount() {
        return predictions.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, distance;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.predictionName);
            distance = itemView.findViewById(R.id.predictionDistance);
        }
    }


}
