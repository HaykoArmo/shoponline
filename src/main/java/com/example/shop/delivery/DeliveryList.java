package com.example.shop.delivery;
import com.example.shop.Entity.*;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.shop.Json.JSONReader;
import com.example.shop.delivery.KMeans;
import com.example.shop.delivery.Cluster;
import java.sql.Timestamp;


import java.util.ArrayList;
import com.example.shop.DAO.OrderDAO;
import com.example.shop.DAO.UserDAO;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import java.util.List;

public class DeliveryList {
    private static final Address WAREHOUSE = new Address(40.184523, 44.522279);
    private static final String API_KEY= "AIzaSyBg7bFGr13qLhoBcOmWuDE2YgzuqpybUFg";
    @Autowired
    private OrderDAO orderDAO;
    @Autowired
    private UserDAO userDAO;
    private List<Order> deliveries;
    private List<Address> deliveryAddresses;

    public void getDeliveriesTimestamp(Timestamp timestamp){
        this.deliveries = orderDAO.getOrdersByDeliveryDate(timestamp);
    }

    public void getDeliveryAddresses(){
        List<Address> addresses = new ArrayList<>();
        for(Order o : deliveries){
            int userID = o.getUserId();
            User user = userDAO.getUser(userID);
            addresses.add(new Address(user.getLatitude(), user.getLongitude()));
        }
        this.deliveryAddresses = addresses;
    }

    //google can only take 23 waypoints at once. Would need to validate and handle if there were too many deliveries at once
    //create delivery routes should only allow this method to be used if there are <= 23 waypoints
    public String createURL(){
        StringBuilder url = new StringBuilder();
        //begins url and adds origin and destination which are both the warehouse
        url.append("https://maps.googleapis.com/maps/api/directions/json?origin=" + WAREHOUSE.getLatitude()
                + "," + WAREHOUSE.getLongitude() + "&destination=" + WAREHOUSE.getLatitude() + ","
                + WAREHOUSE.getLongitude());
        //lets api know can reorder waypoints for efficiency
        url.append("&waypoints=optimize:true");
        //add in waypoints
        for(Address a : deliveryAddresses){
            url.append("|" + a.getLatitude() + "," + a.getLongitude());
        }
        //add in my key
        url.append("&key=" + API_KEY);
        return url.toString();
    }

    public List<Address> convertWaypointsToDirections(String waypoints){
        List<Integer> intWaypoints = stringToIntList(waypoints);
        List<Address> orderedAddresses = new ArrayList<>();
        for(Integer i : intWaypoints){
            orderedAddresses.add(deliveryAddresses.get(i));
        }
        return orderedAddresses;
    }

    public List<Integer> stringToIntList(String waypoints){
        List<String> list = Lists.newArrayList(Splitter.on(",").trimResults().split(waypoints));
        List<Integer> integers = new ArrayList<>();
        for(String s : list){
            integers.add(Integer.parseInt(s));
        }
        return integers;
    }

    public Timestamp setTimestamp(int hour){
        Timestamp timestamp = Timestamp.from(Instant.now());
        timestamp.setHours(hour);
        timestamp.setMinutes(0);
        timestamp.setSeconds(0);
        timestamp.setNanos(0);
        return timestamp;
    }

    public void updateOrderStatus(){
        for(Order o : deliveries){
            o.setOrderStatus(OrderStatus.SHIPPED);
            orderDAO.updateOrderStatus(o);
        }
    }

    public List<Address> createDeliveryRoute(int hour){
        getDeliveriesTimestamp(setTimestamp(hour));
        getDeliveryAddresses();
        if(this.deliveryAddresses.size() == 0){
            return new ArrayList<>();
        }
        else if(this.deliveryAddresses.size() <= 23){
            try{
                String json = JSONReader.readUrl(createURL());
                String waypoints = JSONReader.extractWaypoints(json);
                updateOrderStatus();
                return convertWaypointsToDirections(waypoints);

            } catch(Exception e){
                e.printStackTrace();
                return new ArrayList<>();
            }
        } else {
            KMeans kMeans = createDeliveryRouteClusters();
            kMeans = orderDeliveryClusters(kMeans);
            List<String> urls = createClusterURLs(kMeans);
            List<List<Address>> addressesList = sendClusterRequests(urls);
            List<Address> addresses = new ArrayList<>();
            for(List<Address> a : addressesList){
                addresses.addAll(a);
            }
            updateOrderStatus();
            return addresses;
        }
    }

    private KMeans createDeliveryRouteClusters(){
        int addresses = this.deliveryAddresses.size();
        int clusters = (addresses/23) + 2;
        KMeans kMeans;
        boolean quit;
        do{
            //assume clusters will be valid and quit at end of loop
            quit = true;
            kMeans = new KMeans(clusters, addresses, this.deliveryAddresses);
            kMeans.init();
            kMeans.calculate();
            //check if any cluster has more waypoints than api accepts
            for(Cluster c : kMeans.getClusters()){
                if(c.getAddresses().size() > 23){
                    //clusters not valid, increase number of clusters, run again
                    quit = false;
                    clusters+=4;
                    break;
                }
            }
        } while(!quit);
        return kMeans;
    }

    private double calculateDistance(Address one, Address two){
        double x = Math.abs(one.getLatitude() - two.getLatitude());
        double y = Math.abs(one.getLongitude() - two.getLongitude());
        return Math.sqrt(x*x*y*y);
    }

    //determine order for api calls
    private KMeans orderDeliveryClusters(KMeans kMeans){
        KMeans finalKMeans = new KMeans(kMeans.getNumClusters(), kMeans.getNumPoints(), kMeans.getAddresses());

        //find closest cluster to warehouse to start
        double distance = 12345; //random large number to compare to
        Cluster firstCluster = kMeans.getClusters().get(0);
        for(Cluster c : kMeans.getClusters()){
            double temp  = calculateDistance(WAREHOUSE, c.getCentroid());
            if(temp < distance){
                distance = temp;
                firstCluster = c;
            }
        }
        //put closest cluster first in order, remove from old kMeans
        finalKMeans.getClusters().add(firstCluster);
        kMeans.getClusters().remove(firstCluster);


        //order rest of clusters based on distance from previous cluster
        int size = kMeans.getClusters().size();
        for(int i = 0; i < size; i++){
            distance = 12345; //reset distance
            Cluster cluster = kMeans.getClusters().get(0);
            for(Cluster c : kMeans.getClusters()){
                double temp  = calculateDistance(finalKMeans.getClusters().get(i).getCentroid(), c.getCentroid());
                if(temp < distance){
                    distance = temp;
                    cluster = c;
                }
            }
            //put closest cluster first in order, remove from old kMeans
            finalKMeans.getClusters().add(cluster);
            kMeans.getClusters().remove(cluster);
        }
        return finalKMeans;
    }

    //getNumClusters() is not returning accurate number
    public List<String> createClusterURLs(KMeans kMeans){
        StringBuilder url = new StringBuilder();
        List<String> urls  = new ArrayList<>();
        int count = 1;
        for(int i = 0; i < kMeans.getClusters().size(); i++){
            url.setLength(0);
            if(i == 0){
                //first request will always start from warehouse
                url.append("https://maps.googleapis.com/maps/api/directions/json?origin=" + WAREHOUSE.getLatitude()
                        + "," + WAREHOUSE.getLongitude());
                //destination is center of next cluster
                url.append("&destination=" + kMeans.getClusters().get(i+1).getCentroid().getLatitude() + ","
                        + kMeans.getClusters().get(i+1).getCentroid().getLongitude());
                //lets api know can reorder waypoints for efficiency
                url.append("&waypoints=optimize:true");
                for(Address a : kMeans.getClusters().get(i).getAddresses()){
                    url.append("|" + a.getLatitude() + "," + a.getLongitude());
                }
                //add in my key
                url.append("&key=" + API_KEY);
                if(kMeans.getClusters().get(i).getAddresses().size() != 0){
                    urls.add(url.toString());
                }
            }
            else if(i == kMeans.getClusters().size() - 1){
                url.append("https://maps.googleapis.com/maps/api/directions/json?");
                //origin == prev cluster center
                url.append("origin=" + kMeans.getClusters().get(i-1).getCentroid().getLatitude() + ","
                        + kMeans.getClusters().get(i-1).getCentroid().getLongitude());
                //last cluster destination will always be warehouse
                url.append("&destination=" + WAREHOUSE.getLatitude() + ","
                        + WAREHOUSE.getLongitude());
                //lets api know can reorder waypoints for efficiency
                url.append("&waypoints=optimize:true");
                for(Address a : kMeans.getClusters().get(i).getAddresses()){
                    url.append("|" + a.getLatitude() + "," + a.getLongitude());
                }
                //add in my key
                url.append("&key=" + API_KEY);
                if(kMeans.getClusters().get(i).getAddresses().size() != 0){
                    urls.add(url.toString());
                }
            }
            else {
                url.append("https://maps.googleapis.com/maps/api/directions/json?");
                //origin == prev cluster center
                url.append("origin=" + kMeans.getClusters().get(i-1).getCentroid().getLatitude() + ","
                        + kMeans.getClusters().get(i-1).getCentroid().getLongitude());
                //destination == next cluster center
                url.append("&destination=" + kMeans.getClusters().get(i+1).getCentroid().getLatitude() + ","
                        + kMeans.getClusters().get(i+1).getCentroid().getLongitude());
                //lets api know can reorder waypoints for efficiency
                url.append("&waypoints=optimize:true");
                for(Address a : kMeans.getClusters().get(i).getAddresses()){
                    url.append("|" + a.getLatitude() + "," + a.getLongitude());
                }
                //add in my key
                url.append("&key=" + API_KEY);
                if(kMeans.getClusters().get(i).getAddresses().size() != 0){
                    urls.add(url.toString());
                }
            }
        }
        return urls;
    }

    private List<List<Address>> sendClusterRequests(List<String> urls){
        List<List<Address>> addresses = new ArrayList<>();
        for(String url : urls){
            try{
                String json = JSONReader.readUrl(url);
                String waypoints = JSONReader.extractWaypoints(json);
                addresses.add(convertWaypointsToDirections(waypoints));
            } catch(Exception e){
                e.printStackTrace();
            }
        }
        return addresses;
    }
}
