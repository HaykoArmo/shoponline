package com.example.shop.Entity;

public class UserOrderConverter {
    public static Order convertToOrder(UserOrder userOrder){
        Order order = new Order();
        order.setUserId(userOrder.getUserId());
        order.setAmount(userOrder.getAmount());
        order.setPurchaseDate(userOrder.getPurchaseDate());
        order.setOrderStatus(userOrder.getOrderStatus());
        order.setDeliveryDate(userOrder.getDeliveryDate());
        order.setProducts(userOrder.getProducts());
        return order;
    }
}
