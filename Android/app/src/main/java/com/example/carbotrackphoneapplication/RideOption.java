package com.example.carbotrackphoneapplication;

public class RideOption {
    private String type;
    private String time;
    private String cost;

    public RideOption(String type, String time, String cost) {
        this.type = type;
        this.time = time;
        this.cost = cost;
    }

    public String getType() {
        return type;
    }

    public String getTime() {
        return time;
    }

    public String getCost() {
        return cost;
    }
}
