package com.codecool.shop.dao.implementation.jdbc;

import javax.sql.DataSource;
import java.sql.*;

public abstract class AddressIdProvider {


    protected int getCountryId(String country, DataSource dataSource) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT id FROM country WHERE name = ?";
            PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, country);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt("id");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected int getStateId(String state, DataSource dataSource) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT id FROM state WHERE name = ?";
            PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, state);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt("id");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
