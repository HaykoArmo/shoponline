package com.example.shop.Entity;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
public class UserOrder {
    private int userId;
    private BigDecimal amount;
    private Timestamp purchaseDate;
    private OrderStatus orderStatus;
    private Timestamp deliveryDate;
    private List<Product> products;

    public UserOrder(){
        this.amount = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_CEILING);
        this.products = new ArrayList<>();
    }

    public void updateAmount(Product product){
        amount = amount.add(product.getPrice());
    }

    public void finalizeOrder(int hour){
        this.purchaseDate = Timestamp.from(Instant.now());
        this.orderStatus = OrderStatus.ORDERED;
        this.deliveryDate = chooseDeliveryDate(hour);
    }

    private Timestamp chooseDeliveryDate(int hour){
        Timestamp timestamp = Timestamp.from(Instant.now());
        //change day to tomorrow
        timestamp.setDate(timestamp.getDate()+1);
        timestamp.setHours(hour);
        timestamp.setMinutes(0);
        timestamp.setSeconds(0);
        timestamp.setNanos(0);
        return timestamp;
    }

    public int getUserId(){
        return userId;
    }

    public void setUserId(int userId){
        this.userId = userId;
    }

    public BigDecimal getAmount(){
        return amount;
    }

    public Timestamp getPurchaseDate(){
        return purchaseDate;
    }

    public OrderStatus getOrderStatus(){
        return orderStatus;
    }

    public Timestamp getDeliveryDate(){
        return deliveryDate;
    }

    public List<Product> getProducts(){
        return products;
    }

    public void setProducts(List<Product> products){
        this.products = products;
    }
}
