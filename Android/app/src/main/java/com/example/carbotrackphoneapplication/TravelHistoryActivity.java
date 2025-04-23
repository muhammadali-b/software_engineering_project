package com.example.carbotrackphoneapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class TravelHistoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<TravelItem> travelList = new ArrayList<>();
    TravelAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_history);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ImageView backBtn = findViewById(R.id.back_button);
        backBtn.setOnClickListener(view -> finish());

        travelList.add(new TravelItem("Intercontinental Miami", "Mar 30 · 2:00 PM", "100cc · 100 miles"));
        travelList.add(new TravelItem("Intercontinental Miami", "Mar 30 · 2:00 PM", "100cc · 100 miles"));
        travelList.add(new TravelItem("Intercontinental Miami", "Mar 30 · 2:00 PM", "100cc · 100 miles"));

        adapter = new TravelAdapter(travelList);
        recyclerView.setAdapter(adapter);
    }

    static class TravelItem {
        String location, time, info;
        TravelItem(String location, String time, String info) {
            this.location = location;
            this.time = time;
            this.info = info;
        }
    }

    class TravelAdapter extends RecyclerView.Adapter<TravelAdapter.TravelViewHolder> {

        List<TravelItem> items;
        TravelAdapter(List<TravelItem> items) { this.items = items; }

        @Override
        public TravelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_travel_history, parent, false);
            return new TravelViewHolder(view);
        }

        @Override
        public void onBindViewHolder(TravelViewHolder holder, int position) {
            TravelItem item = items.get(position);
            holder.location.setText(item.location);
            holder.time.setText(item.time);
            holder.info.setText(item.info);
            holder.rebookBtn.setOnClickListener(v ->
                    Toast.makeText(TravelHistoryActivity.this, "Rebook clicked", Toast.LENGTH_SHORT).show()
            );
        }

        @Override
        public int getItemCount() { return items.size(); }

        class TravelViewHolder extends RecyclerView.ViewHolder {
            TextView location, time, info;
            Button rebookBtn;
            TravelViewHolder(View itemView) {
                super(itemView);
                location = itemView.findViewById(R.id.location);
                time = itemView.findViewById(R.id.time);
                info = itemView.findViewById(R.id.creditsMiles);
                rebookBtn = itemView.findViewById(R.id.rebookBtn);
            }
        }
    }
}
