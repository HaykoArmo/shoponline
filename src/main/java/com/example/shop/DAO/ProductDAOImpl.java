package com.example.shop.DAO;

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

@Repository
public class ProductDAOImpl implements ProductDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    @Override
    public List<Product> getProducts() {
        String query = "Select * from product";
        List<Product> products = getJdbcTemplate().query(query, new ProductRowMapper());
        return products;
    }

    @Override
    public Product getProduct(int id) {
        String query = "Select * from product where id=?";
        List<Product> products = getJdbcTemplate().query(query, new ProductRowMapper(), id);
        if (products.size() != 1) {
            return null;
        }
        return products.get(0);
    }

    @Override
    public Product getProduct(String name) {
        String query = "Select * from product where name=?";
        List<Product> products = getJdbcTemplate().query(query, new ProductRowMapper(), name);
        if (products.size() != 1) {
            return null;
        }
        return products.get(0);
    }

    @Override
    public void addProduct(Product product) {
        String insert = "Insert into product(name, price, quantity) values(?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        getJdbcTemplate().update((PreparedStatementCreator) (connection) -> {
            PreparedStatement ps = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, product.getName());
            ps.setBigDecimal(2, product.getPrice());
            ps.setInt(3, product.getQuantity());
            return ps;
        }, keyHolder);
        product.setId(keyHolder.getKey().intValue());
    }

    @Override
    public void updatePrice(Product product) {
        String insert = "Update product set price = ? where id = ?";
        getJdbcTemplate().update(insert, product.getPrice(), product.getId());
    }

    @Override
    public void updateQuantity(Product product) {
        String insert = "Update product set quantity = ? where id = ?";
        getJdbcTemplate().update(insert, product.getQuantity(), product.getId());
    }

    @Override
    public void deleteProduct(int id) {
        // define query arguments
        Object[] params = {id};

        // define SQL types of the arguments
        int[] types = {Types.INTEGER};

        String delete = "Delete from product where id =?";
        getJdbcTemplate().update(delete, params, types);
    }
}

class ProductRowMapper implements RowMapper<Product> {
    @Override
    public Product mapRow(ResultSet resultSet, int i) throws SQLException {
        Product product = new Product();
        product.setId(resultSet.getInt("id"));
        product.setName(resultSet.getString("name"));
        product.setPrice(resultSet.getBigDecimal("price"));
        product.setQuantity(resultSet.getInt("quantity"));
        return product;
    }
}

