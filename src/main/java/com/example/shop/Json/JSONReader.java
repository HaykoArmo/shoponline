package com.example.shop.Json;
import com.example.shop.delivery.Address;

import java.io.*;
import java.net.URL;
public class JSONReader {
    public static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }

    public static String extractWaypoints(String json){
        String waypoint_order = json.substring(json.indexOf("waypoint_order"));
        waypoint_order = waypoint_order.substring(waypoint_order.indexOf("[")+1);
        waypoint_order = waypoint_order.substring(0, waypoint_order.indexOf(']'));
        return waypoint_order;
    }

    public static Address extractGeocode(String json){
        String location = json.substring(json.indexOf("lat"));
        location = location.substring(0, location.indexOf('}'));
        double latitude = Double.parseDouble(location.substring(7, location.indexOf(',')));
        double longitude = Double.parseDouble(location.substring(location.indexOf("lng") + 7));
        return new Address(latitude, longitude);
    }
}
