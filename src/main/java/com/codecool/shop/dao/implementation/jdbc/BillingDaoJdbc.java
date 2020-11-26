package com.codecool.shop.dao.implementation.jdbc;

import com.codecool.shop.dao.DatabaseManager;
import com.codecool.shop.dao.contracts.BillingDao;
import com.codecool.shop.model.BillingData;
import com.codecool.shop.model.LineItem;
import com.codecool.shop.model.User;
import com.codecool.shop.util.ResultSetHelper;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BillingDaoJdbc extends AddressIdProvider implements BillingDao {

    private static BillingDaoJdbc instance = null;
    DataSource dataSource;

    private BillingDaoJdbc() {
        DatabaseManager dbManager = DatabaseManager.getInstance();
        dataSource = dbManager.getDataSource();
    }

    private BillingDaoJdbc(String configFileName) {
        DatabaseManager dbManager = DatabaseManager.getInstance(configFileName);
        dataSource = dbManager.getDataSource();
    }

    public static BillingDaoJdbc getInstance() {
        if (instance == null) {
            instance = new BillingDaoJdbc();
        }
        return instance;
    }

    public static BillingDaoJdbc getInstance(String configFileName) {
        if (instance == null) {
            instance = new BillingDaoJdbc(configFileName);
        }
        return instance;
    }

    @Override
    public BillingData find(int userId) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT first_name, last_name, email, address, country.name, state.name, zip_code\n" +
                    "FROM billing_address\n" +
                    "    JOIN country ON billing_address.country_id = country.id\n" +
                    "    JOIN state on billing_address.state_id = state.id\n" +
                    "WHERE billing_address.user_id = ?";
            PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            List<BillingData> billingDatas = buildBillingAddresses(new ResultSetHelper(resultSet));
            return billingDatas.size() >= 1 ? billingDatas.get(0) : null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<BillingData> buildBillingAddresses(ResultSetHelper helper) throws SQLException {
        List<BillingData> addresses = new ArrayList<>();
        ResultSet resultSet = helper.getResultSet();
        while (resultSet.next()) {
            BillingData data = new BillingData(
                    resultSet.getString(helper.getColumnIndex("billing_address.first_name")),
                    resultSet.getString(helper.getColumnIndex("billing_address.last_name")),
                    resultSet.getString(helper.getColumnIndex("billing_address.email")),
                    resultSet.getString(helper.getColumnIndex("billing_address.address")),
                    resultSet.getString(helper.getColumnIndex("country.name")),
                    resultSet.getString(helper.getColumnIndex("state.name")),
                    resultSet.getString(helper.getColumnIndex("billing_address.zip_code"))
            );
            addresses.add(data);
        }
        return addresses;
    }

    @Override
    public int update(BillingData billingData, int userId) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "UPDATE billing_address SET first_name = ?, last_name = ?, email = ?," +
                    "address = ?, country_id = ?, state_id = ?, zip_code =? WHERE user_id = ? RETURNING id";
            PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, billingData.getFirstName());
            statement.setString(2, billingData.getLastName());
            statement.setString(3, billingData.getEmail());
            statement.setString(4, billingData.getAddress());
            statement.setInt(5, getCountryId(billingData.getCountry(), dataSource));
            statement.setInt(6, getStateId(billingData.getState(), dataSource));
            statement.setString(7, billingData.getZip());
            statement.setInt(8, userId);
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            resultSet.next();
            return resultSet.getInt("id");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public int add(BillingData billingData,int userId) {
        if (hasUserSavedBillingAddress(userId)) {
            return update(billingData, userId);
                }
        else {
            return addBillingData(billingData, userId);
                }
    }

    private int addBillingData(BillingData billingData, int userId) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "INSERT INTO billing_address (user_id, first_name, last_name, email, address, country_id, state_id, zip_code)  VALUES (?,?,?,?,?,?,?,?) RETURNING id";
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

    public boolean hasUserSavedBillingAddress(int userId) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT CASE WHEN EXISTS (SELECT * FROM billing_address WHERE user_id = ?) THEN 'TRUE' ELSE 'FALSE' END";
            PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getString(1).equals("TRUE");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<BillingData> getAll(int userId, ResultSetHelper helper) {
        List<BillingData> billingData = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT first_name, last_name, email, address, country.name, state.name, zip_code\n" +
                    "FROM billing_address\n" +
                    "    JOIN country ON billing_address.country_id = country.id\n" +
                    "    JOIN state on billing_address.state_id = state.id\n" +
                    "WHERE billing_address.user_id = ?";
            PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                BillingData data = new BillingData(
                        resultSet.getString(helper.getColumnIndex("billing_address.first_name")),
                        resultSet.getString(helper.getColumnIndex("billing_address.last_name")),
                        resultSet.getString(helper.getColumnIndex("billing_address.email")),
                        resultSet.getString(helper.getColumnIndex("billing_address.address")),
                        resultSet.getString(helper.getColumnIndex("country.name")),
                        resultSet.getString(helper.getColumnIndex("state.name")),
                        resultSet.getString(helper.getColumnIndex("billing_address.zip_code"))
                );
                billingData.add(data);
            } return billingData;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}


