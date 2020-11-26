package com.codecool.shop.dao.implementation.jdbc;

import com.codecool.shop.dao.DatabaseManager;
import com.codecool.shop.dao.contracts.UserDao;
import com.codecool.shop.model.User;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

public class UserDaoJdbc implements UserDao {

    DataSource dataSource;

    private static UserDaoJdbc instance = null;

    private UserDaoJdbc() {
        DatabaseManager dbManager = DatabaseManager.getInstance();
        dataSource = dbManager.getDataSource();
    }

    private UserDaoJdbc(String configFileName) {
        DatabaseManager dbManager = DatabaseManager.getInstance(configFileName);
        dataSource = dbManager.getDataSource();
    }

    public static UserDaoJdbc getInstance() {
        if (instance==null) {
            instance = new UserDaoJdbc();
        }
        return instance;
    }

    public static UserDaoJdbc getInstance(String configFileName) {
        if (instance==null) {
            instance = new UserDaoJdbc(configFileName);
        }
        return instance;
    }

    public String getPass(String username){
        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT password FROM \"user\" WHERE email LIKE ?";
            PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if(!resultSet.next()){
                return null;
            };
            return resultSet.getString("password");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User find(String email) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT * FROM \"user\" WHERE email = ?";
            PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if(!resultSet.next()){
                return null;
            };
            return new User(resultSet.getString("name") ,resultSet.getInt("id"),resultSet.getString("email"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int add(User user, String hash) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "INSERT INTO \"user\" (name, email ,password) VALUES (?,?,?) RETURNING id";
            PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, hash);
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            resultSet.next();
            return resultSet.getInt("id");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(User user) {

    }

    @Override
    public List<User> getAll() {
        return null;
    }
}
