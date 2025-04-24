package com.example.carbotrackphoneapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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

        adapter = new TravelAdapter(travelList);
        recyclerView.setAdapter(adapter);

        fetchTripData();
    }

    private void fetchTripData() {
        new Thread(() -> {
            try {
                SharedPreferences prefs = getSharedPreferences("CarboTrackPrefs", MODE_PRIVATE);
                int employeeId = prefs.getInt("employee_id", -1);
                if (employeeId == -1) {
                    runOnUiThread(() -> Toast.makeText(this, "Employee ID not found", Toast.LENGTH_SHORT).show());
                    return;
                }

                URL url = new URL("https://softwareengineeringproject-production.up.railway.app/api/employee-miles/" + employeeId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) response.append(line);
                reader.close();

                JSONObject json = new JSONObject(response.toString());
                JSONArray data = json.getJSONArray("data");

                travelList.clear();
                for (int i = 0; i < data.length(); i++) {
                    JSONObject trip = data.getJSONObject(i);
                    String vehicleType = trip.getString("vehicle_type");
                    int miles = trip.getInt("miles");
                    int credits = trip.getInt("carbon_credits");
                    String recordedAt = trip.getString("recorded_at");

                    String info = credits + "cc · " + miles + " miles";
                    travelList.add(new TravelItem(vehicleType, recordedAt, info));
                }

                runOnUiThread(() -> adapter.notifyDataSetChanged());

            } catch (Exception e) {
                Log.e("TripHistory", "Error fetching trip data: " + e.getMessage());
                runOnUiThread(() -> Toast.makeText(this, "Failed to fetch trip data", Toast.LENGTH_SHORT).show());
            }
        }).start();
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
