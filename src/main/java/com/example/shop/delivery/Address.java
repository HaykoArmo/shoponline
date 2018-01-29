package com.example.shop.delivery;

import java.util.Random;

public class Address {
    private double latitude;
    private double longitude;
    private int cluster_number = 0;

    public Address(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getCluster_number(){
        return cluster_number;
    }

    public void setLatitude(double latitude){
        this.latitude = latitude;
    }

    public void setLongitude(double longitude){
        this.longitude = longitude;
    }

    public void setCluster_number(int cluster_number){
        this.cluster_number = cluster_number;
    }

    //Calculates the distance between two Address.
    protected static double distance(Address p, Address centroid) {
        return Math.sqrt(Math.pow((centroid.getLongitude() - p.getLongitude()), 2) + Math.pow((centroid.getLatitude() - p.getLatitude()), 2));
    }

    //Creates random Address
    protected static Address createRandomAddress(Address min, Address max) {
        Random r = new Random();
        double lat = min.getLatitude() + (max.getLatitude() - min.getLatitude()) * r.nextDouble();
        double lon = min.getLongitude() + (max.getLongitude() - min.getLongitude()) * r.nextDouble();
        return new Address(lat,lon);
    }

    @Override
    public String toString(){
        return "Address{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
