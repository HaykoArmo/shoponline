package com.example.shop.DAO;

import com.example.shop.Entity.Order;
import com.example.shop.Entity.OrderStatus;
import com.example.shop.Entity.User;

import java.security.Timestamp;
import java.util.List;

public interface OrderDAO {
    List<Order> getOrders();
    List<Order> getOrdersByUser(User user);
    List<Order> getOrdersByPurchaseDate(Timestamp timestamp);
    List<Order> getOrdersByDeliveryDate(Timestamp timestamp);
    List<Order> getOrdersByStatus(OrderStatus orderStatus);
    Order getOrder(int id);
    void addOrder(Order order);
    //later add cancel(delete)order and update order
    void updateOrderStatus(Order order);
}
