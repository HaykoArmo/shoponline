package com.example.shop.DAO;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import com.example.shop.Entity.OrderStatus;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import com.example.shop.Entity.Order;
import com.example.shop.Entity.OrderList;
import com.example.shop.Entity.OrderStatus;
import com.example.shop.Entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import java.security.Timestamp;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

import com.example.shop.Entity.Order;
import com.example.shop.Entity.OrderList;
import com.example.shop.Entity.Product;
import com.example.shop.DAO.OrderListDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import com.example.shop.Entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;

import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
@Repository
public class OrderDAOImpl implements OrderDAO {
    @Autowired
    private OrderListDAO orderListDAO;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public JdbcTemplate getJdbcTemplate(){
        return jdbcTemplate;
    }

    @Override
    public List<Order> getOrders(){
        String query = "Select * from `order`";
        List<Order> orders = getJdbcTemplate().query(query, new OrderRowMapper());
        for(Order o : orders){
            OrderList products = orderListDAO.getProductsForOrder(o.getId());
            o.setProducts(products.getProductList());
        }
        return orders;
    }

    @Override
    public List<Order> getOrdersByUser(User user){
        String query = "Select * from `order` where user_id = ?";
        List<Order> orders = getJdbcTemplate().query(query, new OrderRowMapper(), user.getId());
        for(Order o : orders){
            OrderList products = orderListDAO.getProductsForOrder(o.getId());
            o.setProducts(products.getProductList());
        }
        return orders;
    }

    @Override
    public List<Order> getOrdersByPurchaseDate(Timestamp timestamp){
        String query = "Select * from `order` where purchase_date = ?";
        List<Order> orders = getJdbcTemplate().query(query, new OrderRowMapper(), timestamp);
        for(Order o : orders){
            OrderList products = orderListDAO.getProductsForOrder(o.getId());
            o.setProducts(products.getProductList());
        }
        return orders;
    }

    @Override
    public List<Order> getOrdersByDeliveryDate(Timestamp timestamp){
        String query = "Select * from `order` where delivery_date = ?";
        List<Order> orders = getJdbcTemplate().query(query, new OrderRowMapper(), timestamp);
        for(Order o : orders){
            OrderList products = orderListDAO.getProductsForOrder(o.getId());
            o.setProducts(products.getProductList());
        }
        return orders;
    }

    @Override
    public List<Order> getOrdersByStatus(OrderStatus orderStatus){
        String query = "Select * from `order` where status = ?";
        List<Order> orders = getJdbcTemplate().query(query, new OrderRowMapper(), orderStatus.ordinal());
        for(Order o : orders){
            OrderList products = orderListDAO.getProductsForOrder(o.getId());
            o.setProducts(products.getProductList());
        }
        return orders;
    }

    @Override
    public Order getOrder(int id){
        String query = "Select * from `order` where id = ?";
        List<Order> orders = getJdbcTemplate().query(query, new OrderRowMapper(), id);
        if(orders.size() != 1){
            return null;
        }
        //have confirmed only one order in list
        OrderList products = orderListDAO.getProductsForOrder(orders.get(0).getId());
        orders.get(0).setProducts(products.getProductList());

        return orders.get(0);
    }

    @Override
    public void addOrder(Order order){
        String insert = "Insert into `order`(user_id, amount, purchase_date, status, delivery_date) values(?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        getJdbcTemplate().update((PreparedStatementCreator) (connection) -> {
            PreparedStatement ps = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, order.getUserId());
            ps.setBigDecimal(2, order.getAmount());
            ps.setTimestamp(3, order.getPurchaseDate());
            ps.setInt(4, order.getOrderStatus().ordinal());
            ps.setTimestamp(5, order.getDeliveryDate());
            return ps;
        }, keyHolder);
        order.setId(keyHolder.getKey().intValue());
        //put all products from order in orderlist table for storage
        orderListDAO.addProductsFromOrder(order);
    }

    @Override
    public void updateOrderStatus(Order order){
        String update = "Update `order` set status = ? where id = ?";
        getJdbcTemplate().update(update, order.getOrderStatus().ordinal(), order.getId());
    }
}

class OrderRowMapper implements RowMapper<Order>{
    @Override
    public Order mapRow(ResultSet resultSet, int i) throws SQLException {
        Order order = new Order();
        order.setId(resultSet.getInt("id"));
        order.setUserId(resultSet.getInt("user_id"));
        order.setAmount(resultSet.getBigDecimal("amount"));
        order.setPurchaseDate(resultSet.getTimestamp("purchase_date"));

        switch(resultSet.getInt("status")){
            case 0: order.setOrderStatus(OrderStatus.ORDERED);break;
            case 1: order.setOrderStatus(OrderStatus.SHIPPED);break;
            case 2: order.setOrderStatus(OrderStatus.DELIVERED);break;
        }

        order.setDeliveryDate(resultSet.getTimestamp("delivery_date"));
        return order;
    }
}

