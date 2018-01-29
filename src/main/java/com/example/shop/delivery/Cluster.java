package com.example.shop.delivery;

import java.util.List;

import java.util.ArrayList;

/**
 * Adapted from dataonfocus.com example
 */
public class Cluster {
    public List<Address> addresses;
    public Address centroid;
    public int id;

    //Creates a new Cluster
    public Cluster(int id) {
        this.id = id;
        this.addresses = new ArrayList();
        this.centroid = null;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void addAddress(Address address) {
        addresses.add(address);
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    public Address getCentroid() {
        return centroid;
    }

    public void setCentroid(Address centroid) {
        this.centroid = centroid;
    }

    public int getId() {
        return id;
    }

    public void clear() {
        addresses.clear();
    }

    public void plotCluster() {
        System.out.println("[Cluster: " + id + "]");
        System.out.println("[Centroid: " + centroid + "]");
        System.out.println("[Addresses: \n");
        for (Address a : addresses) {
            System.out.println(a);
        }
        System.out.println("]");
    }
}
