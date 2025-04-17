package com.example.carbotrackphoneapplication;

public class Prediction {
    private String name;
    private double lat;
    private double lon;
    private String distance;

    public Prediction (String name, double lat, double lon, String distance) {
        this.name = name;
        this.lat = lat;
        this.lon =  lon;
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public String getDistance() {
        return distance;
    }


}

