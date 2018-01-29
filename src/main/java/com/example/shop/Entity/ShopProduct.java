package com.example.shop.Entity;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
public class ShopProduct {
    private String name;
    private BigDecimal price;
    private int quantity;

    public ShopProduct(String name, BigDecimal price, int quantity){
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getName(){
        return name;
    }

    public BigDecimal getPrice(){
        return price;
    }

    public int getQuantity(){
        return quantity;
    }
}
