package com.example.shop.delivery;

import java.util.ArrayList;
import java.util.List;

public class KMeans {
    //clusters and points both need to be determined based on number of deliveries
    //Number of Clusters. This metric should be related to the number of points
    private int numClusters;
    //Number of Points
    private int numPoints;
    //Min and Max X and Y\
    //This will confine the delivery area
    //needs to be in terms of addresses
    private static final Address MIN_ADDRESS = new Address(40.1246, 44.5095);
    private static final Address MAX_ADDRESS = new Address(40.2190, 44.5378);
//    private static final int MIN_COORDINATE = 0;
//    private static final int MAX_COORDINATE = 10;

    private List<Address> addresses;
    private List<Cluster> clusters;

    public KMeans(int numClusters, int numPoints, List<Address> addresses) {
        this.numClusters = numClusters;
        this.numPoints = numPoints;
        this.addresses = addresses;
        this.clusters = new ArrayList<>();
    }

    //Initializes the process
    public void init() {
        //Create Clusters
        //Set Random Centroids
        for (int i = 0; i < numClusters; i++) {
            Cluster cluster = new Cluster(i);
            Address centroid = Address.createRandomAddress(MIN_ADDRESS, MAX_ADDRESS);
            cluster.setCentroid(centroid);
            clusters.add(cluster);
        }
    }

    //The process to calculate the K Means, with iterating method.
    public void calculate() {
        boolean finish = false;
        int iteration = 0;

        // Add in new data, one at a time, recalculating centroids with each new one.
        while (!finish) {
            //Clear cluster state
            clearClusters();

            List<Address> lastCentroids = getCentroids();

            //Assign points to the closer cluster
            assignCluster();

            //Calculate new centroids.
            calculateCentroids();

            iteration++;

            List<Address> currentCentroids = getCentroids();

            //Calculates total distance between new and old Centroids
            double distance = 0;
            for (int i = 0; i < lastCentroids.size(); i++) {
                distance += Address.distance(lastCentroids.get(i), currentCentroids.get(i));
            }

            if (distance == 0) {
                finish = true;
            }
        }
    }

    private void clearClusters() {
        for (Cluster cluster : clusters) {
            cluster.clear();
        }
    }

    private List<Address> getCentroids() {
        List<Address> centroids = new ArrayList<>(numClusters);
        for (Cluster cluster : clusters) {
            Address aux = cluster.getCentroid();
            Address point = new Address(aux.getLatitude(), aux.getLongitude());
            centroids.add(point);
        }
        return centroids;
    }

    private void assignCluster() {
        double max = Double.MAX_VALUE;
        double min = max;
        int cluster = 0;
        double distance = 0.0;

        for (Address a : addresses) {
            min = max;
            for (int i = 0; i < numClusters; i++) {
                Cluster c = clusters.get(i);
                distance = Address.distance(a, c.getCentroid());
                if (distance < min) {
                    min = distance;
                    cluster = i;
                }
            }
            a.setCluster_number(cluster);
            clusters.get(cluster).addAddress(a);
        }
    }

    private void calculateCentroids() {
        for (Cluster cluster : clusters) {
            double sumX = 0;
            double sumY = 0;
            List<Address> list = cluster.getAddresses();
            int n_points = list.size();

            for (Address a : list) {
                sumX += a.getLatitude();
                sumY += a.getLongitude();
            }

            Address centroid = cluster.getCentroid();
            if (n_points > 0) {
                double newX = sumX / n_points;
                double newY = sumY / n_points;
                centroid.setLatitude(newX);
                centroid.setLongitude(newY);
            }
        }
    }

    public List<Cluster> getClusters() {
        return clusters;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public int getNumClusters() {
        return numClusters;
    }

    public int getNumPoints() {
        return numPoints;
    }
}
