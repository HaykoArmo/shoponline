package com.example.shop.backoffice;

import com.example.shop.DAO.OrderDAO;
import com.example.shop.DAO.ProductDAO;
import com.example.shop.DAO.UserDAO;
import com.example.shop.Entity.*;
import com.example.shop.delivery.Address;
import com.example.shop.delivery.DeliveryList;
import org.springframework.beans.factory.annotation.Autowired;
import java.math.BigDecimal;
import java.util.List;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import com.example.shop.DAO.UserDAO;
import com.example.shop.DAO.OrderDAO;
import com.example.shop.Entity.User;
import com.example.shop.Entity.Product;
import com.example.shop.DAO.ProductDAO;
import org.springframework.beans.factory.annotation.Autowired;
public class AdminSession {
    @Autowired
    private ProductInfoForm productInfoForm;
    @Autowired
    private ProductDAO productDAO;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private OrderDAO orderDAO;
    @Autowired
    private DeliveryList deliveryList;


    public void addProduct(String name, BigDecimal price, int quantity){
        productInfoForm.createProduct(name, price, quantity);
        Product product = ShopProductConverter.convertToProduct(productInfoForm.getShopProduct());
        //product does not already exist
        if(productDAO.getProduct(product.getName()) == null){
            productDAO.addProduct(product);
        }
    }

    public List<User> viewUsers(){
        return userDAO.getUsers();
    }

    public List<Product> viewProducts(){
        return productDAO.getProducts();
    }

    public List<Order> viewOrders(){
        return orderDAO.getOrders();
    }

    public List<Order> viewOrdersByStatus(OrderStatus orderStatus){
        return orderDAO.getOrdersByStatus(orderStatus);
    }

    public void deleteUser(int id){
        userDAO.deleteUser(id);
    }

    public void deleteProduct(int prodId){
        productDAO.deleteProduct(prodId);
    }

    public List<Address> deliverOrders(int hour){
        return deliveryList.createDeliveryRoute(hour);
    }
}
