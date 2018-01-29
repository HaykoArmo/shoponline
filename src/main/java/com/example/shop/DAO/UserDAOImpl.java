package com.example.shop.DAO;

import com.example.shop.Entity.Order;
import com.example.shop.Entity.OrderList;
import com.example.shop.Entity.OrderStatus;
import com.example.shop.Entity.User;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;

@Repository
public class UserDAOImpl implements UserDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    @Override
    public List<User> getUsers() {

        String query = "Select * from user";
        List<User> userList = getJdbcTemplate().query(query, new UserRowMapper());
        return userList;
    }

    @Override
    public User getUser(int id) {

        String query = "Select * from user where id=?";
        List<User> user = getJdbcTemplate().query(query, new UserRowMapper(), id);

        if (user.size() != 1) {
            return null;
        }
        return user.get(0);
    }

    @Override
    public User getUser(String email) {
        String query = "Select * from user where email=?";
        List<User> user = getJdbcTemplate().query(query, new UserRowMapper(), email);
        if (user.size() != 1) {
            return null;
        }
        return user.get(0);
    }

    @Override
    public void addUser(User user) {
        String insert = "Insert into user(first_name, last_name, email, password, latitude," +
                " longitude, balance, last_login) values (?,?,?,?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        getJdbcTemplate().update((PreparedStatementCreator) (connection) -> {
            PreparedStatement ps = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPassword());
            ps.setDouble(5, user.getLatitude());
            ps.setDouble(6, user.getLongitude());
            ps.setBigDecimal(7, user.getBalance());
            ps.setTimestamp(8, user.getLastLogin());
            return ps;
        }, keyHolder);
        user.setId(keyHolder.getKey().intValue());
    }

    @Override
    public void updatePassword(User user) {
        String insert = "Update user set password = ? where id = ?";
        getJdbcTemplate().update(insert, user.getPassword(), user.getId());
    }

    @Override
    public void updateLastLogin(User user) {
        String insert = "Update user set last_login = ? where id = ?";
        getJdbcTemplate().update(insert, user.getLastLogin(), user.getId());
    }

    @Override
    public void updateBalance(User user) {
        String insert = "Update user set balance = ? where id = ?";
        getJdbcTemplate().update(insert, user.getBalance(), user.getId());
    }

    @Override
    public void deleteUser(int id) {
        // define query arguments
        Object[] params = {id};

        // define SQL types of the arguments
        int[] types = {Types.INTEGER};

        String delete = "Delete from user where id =?";
        getJdbcTemplate().update(delete, params, types);
    }

}

class UserRowMapper implements RowMapper<User> {
    public User mapRow(ResultSet resultSet, int rowNumber) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setFirstName(resultSet.getString("first_name"));
        user.setLastName(resultSet.getString("last_name"));
        user.setEmail(resultSet.getString("email"));
        user.setPassword(resultSet.getString("password"));
        user.setLatitude(resultSet.getDouble("latitude"));
        user.setLongitude(resultSet.getDouble("longitude"));
        user.setBalance(resultSet.getBigDecimal("balance"));
        user.setLastLogin(resultSet.getTimestamp("last_login"));
        return user;
    }
}

