package com.codecool.shop.dao.implementation.jdbc;

import com.codecool.shop.dao.DatabaseManager;
import com.codecool.shop.dao.contracts.ShippingDao;
import com.codecool.shop.model.BillingData;
import com.codecool.shop.model.ShippingData;
import com.codecool.shop.model.User;
import com.codecool.shop.util.ResultSetHelper;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShippingDaoJdbc extends AddressIdProvider implements ShippingDao {

    private static ShippingDaoJdbc instance = null;
    DataSource dataSource;

    private ShippingDaoJdbc() {
        DatabaseManager dbManager = DatabaseManager.getInstance();
        dataSource = dbManager.getDataSource();
    }

    private ShippingDaoJdbc(String configFileName) {
        DatabaseManager dbManager = DatabaseManager.getInstance(configFileName);
        dataSource = dbManager.getDataSource();
    }

    public static ShippingDaoJdbc getInstance() {
        if (instance == null) {
            instance = new ShippingDaoJdbc();
        }
        return instance;
    }

    public static ShippingDaoJdbc getInstance(String configFileName) {
        if (instance == null) {
            instance = new ShippingDaoJdbc(configFileName);
        }
        return instance;
    }

    @Override
    public ShippingData find(int userId) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT first_name, last_name, email, address, country.name, state.name, zip_code\n" +
                    "FROM shipping_address\n" +
                    "    JOIN country ON shipping_address.country_id = country.id\n" +
                    "    JOIN state on shipping_address.state_id = state.id\n" +
                    "WHERE shipping_address.user_id = ?";
            PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            List<ShippingData> shippingData = buildShippingAddresses(new ResultSetHelper(resultSet));
            return shippingData.size() >= 1 ? shippingData.get(0) : null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<ShippingData> buildShippingAddresses(ResultSetHelper helper) throws SQLException {
        List<ShippingData> addresses = new ArrayList<>();
        ResultSet resultSet = helper.getResultSet();
        while (resultSet.next()) {
            ShippingData data = new ShippingData(
                    resultSet.getString(helper.getColumnIndex("shipping_address.first_name")),
                    resultSet.getString(helper.getColumnIndex("shipping_address.last_name")),
                    resultSet.getString(helper.getColumnIndex("shipping_address.email")),
                    resultSet.getString(helper.getColumnIndex("shipping_address.address")),
                    resultSet.getString(helper.getColumnIndex("country.name")),
                    resultSet.getString(helper.getColumnIndex("state.name")),
                    resultSet.getString(helper.getColumnIndex("shipping_address.zip_code"))
            );
            addresses.add(data);
        }
        return addresses;
    }

    @Override
    public int update(ShippingData shippingData, int userId) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "UPDATE shipping_address SET first_name = ?, last_name = ?, email = ?," +
                    "address = ?, country_id = ?, state_id = ?, zip_code =? WHERE user_id = ? RETURNING id";
            PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, shippingData.getFirstName());
            statement.setString(2, shippingData.getLastName());
            statement.setString(3, shippingData.getEmail());
            statement.setString(4, shippingData.getAddress());
            statement.setInt(5, getCountryId(shippingData.getCountry(), dataSource));
            statement.setInt(6, getStateId(shippingData.getState(), dataSource));
            statement.setString(7, shippingData.getZip());
            statement.setInt(8, userId);
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            resultSet.next();
            return resultSet.getInt("id");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private int addShippingData(ShippingData billingData, int userId) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "INSERT INTO shipping_address (user_id, first_name, last_name, email, address, country_id, state_id, zip_code)  VALUES (?,?,?,?,?,?,?,?) RETURNING id";
            PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(2, billingData.getFirstName());
            statement.setString(3, billingData.getLastName());
            statement.setString(4, billingData.getEmail());
            statement.setString(5, billingData.getAddress());
            statement.setInt(6, getCountryId(billingData.getCountry(), dataSource));
            statement.setInt(7, getStateId(billingData.getState(), dataSource));
            statement.setString(8, billingData.getZip());
            statement.setInt(1, userId);
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            resultSet.next();
            return resultSet.getInt("id");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public int add(ShippingData shippingData, int userId) {
        if (hasShippingAddress(userId)) {
            return update(shippingData, userId);
        }
        else {
            return addShippingData(shippingData, userId);
        }
    }


    @Override
    public List<ShippingData> getAll() {
        return null;
    }

    public boolean hasShippingAddress(int userId) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT CASE WHEN EXISTS (SELECT * FROM shipping_address WHERE user_id = ?) THEN 'TRUE' ELSE 'FALSE' END";
            PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getString(1).equals("TRUE");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
