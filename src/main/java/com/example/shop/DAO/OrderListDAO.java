package com.example.shop.DAO;

import com.example.shop.Entity.Order;
import com.example.shop.Entity.OrderList;

public interface OrderListDAO {
    OrderList getProductsForOrder(int orderId);
    void addProductsFromOrder(Order order);
}
