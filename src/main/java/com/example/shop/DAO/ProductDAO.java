package com.example.shop.DAO;

import com.example.shop.Entity.Product;
import java.sql.*;
import java.util.List;

public interface ProductDAO {
    List<Product> getProducts();
    Product getProduct(int id);
    Product getProduct(String name);
    void addProduct(Product product);
    void updatePrice(Product product);
    void updateQuantity(Product product);
    void deleteProduct(int id);
}
