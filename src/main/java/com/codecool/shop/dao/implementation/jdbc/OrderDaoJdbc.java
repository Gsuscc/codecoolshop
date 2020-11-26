package com.codecool.shop.dao.implementation.jdbc;

import com.codecool.shop.dao.DatabaseManager;
import com.codecool.shop.dao.contracts.OrderDao;
import com.codecool.shop.model.Checkout;
import com.codecool.shop.model.LineItem;
import com.codecool.shop.model.Order;
import com.codecool.shop.model.User;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderDaoJdbc extends AddressIdProvider implements OrderDao {

    private static OrderDaoJdbc instance = null;
    DataSource dataSource;

    private OrderDaoJdbc() {
        DatabaseManager dbManager = DatabaseManager.getInstance();
        dataSource = dbManager.getDataSource();
    }

    private OrderDaoJdbc(String configFileName) {
        DatabaseManager dbManager = DatabaseManager.getInstance(configFileName);
        dataSource = dbManager.getDataSource();
    }

    public static OrderDaoJdbc getInstance() {
        if (instance == null) {
            instance = new OrderDaoJdbc();
        }
        return instance;
    }

    public static OrderDaoJdbc getInstance(String configFileName) {
        if (instance == null) {
            instance = new OrderDaoJdbc(configFileName);
        }
        return instance;
    }

    @Override
    public void add(Checkout order, User user, List<LineItem> products, double totalPrice) {
        int billId = addBillingAddress(order);
        int shippId = addShippingAddress(order);
        int orderId = addOrder(billId, shippId, user, totalPrice);
        products.forEach(lineItem -> addProductsToOrder(orderId, lineItem));

    }

    private void addProductsToOrder(int orderId, LineItem product) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "INSERT INTO order_products (order_id, product_id, quantity) VALUES (?,?,?) ";
            PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, orderId);
            statement.setInt(2, product.getProductId());
            statement.setInt(3, product.getQuantity());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private int addOrder(int billId, int shippId, User user, double totalPrice) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "INSERT INTO \"order\" (billing_address_id, shipping_address_id, user_id, total_price, order_date) VALUES (?,?,?,?,?) RETURNING id";
            PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, billId);
            statement.setInt(2, shippId);
            statement.setInt(3, user!= null? user.getId(): 0);
            statement.setDouble(4, totalPrice);
            statement.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            resultSet.next();
            return resultSet.getInt("id");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private int addBillingAddress(Checkout order) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "INSERT INTO billing_address(user_id, first_name, last_name, email, address, country_id, state_id, zip_code) VALUES (?,?,?,?,?,?,?,?) RETURNING id";
            PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, 1);
            statement.setString(2, order.getBillingData().getFirstName());
            statement.setString(3, order.getBillingData().getLastName());
            statement.setString(4, order.getBillingData().getEmail());
            statement.setString(5, order.getBillingData().getAddress());
            statement.setInt(6, getCountryId(order.getBillingData().getCountry(), dataSource));
            statement.setInt(7, getStateId(order.getBillingData().getState(), dataSource));
            statement.setString(8, order.getBillingData().getZip());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            resultSet.next();
            return resultSet.getInt("id");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private int addShippingAddress(Checkout order) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "INSERT INTO shipping_address(user_id, first_name, last_name, email, address, country_id, state_id, zip_code) VALUES (?,?,?,?,?,?,?,?) RETURNING id";
            PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, 1);
            statement.setString(2, order.getShippingData().getFirstName());
            statement.setString(3, order.getShippingData().getLastName());
            statement.setString(4, order.getShippingData().getEmail());
            statement.setString(5, order.getShippingData().getAddress());
            statement.setInt(6, getCountryId(order.getShippingData().getCountry(), dataSource));
            statement.setInt(7, getStateId(order.getShippingData().getState(), dataSource));
            statement.setString(8, order.getShippingData().getZip());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            resultSet.next();
            return resultSet.getInt("id");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Checkout find(int id) {
        return null;
    }

    @Override
    public void remove(int id) {

    }

    public List<LineItem> getOrderDetails(int id) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT product.id, product.name, order_products.quantity, product.default_price, (order_products.quantity * product.default_price) AS total,\n" +
                    "       currency_type\n" +
                    "FROM product JOIN order_products on product.id = order_products.product_id\n" +
                    "JOIN currency c on product.currency_id = c.id\n" +
                    "WHERE order_id = ?";
            PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            return orderItems(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Checkout> getAll() {
        return null;
    }

    @Override
    public List<Order> getAllByUser(int userId) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT id, order_date, total_price FROM \"order\" WHERE user_id = (?)";
            PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            return getOrders(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Order> getOrders(ResultSet resultSet) throws SQLException {
        List<Order> orders = new ArrayList<>();
        while (resultSet.next()) {
            Order order = new Order(resultSet.getInt("id"), resultSet.getTimestamp("order_date"), resultSet.getDouble("total_price"));
            orders.add(order);
        }
        return orders;
    }

    private List<LineItem> orderItems(ResultSet resultSet) throws SQLException {
        List<LineItem> orderItems = new ArrayList<>();
        while (resultSet.next()) {
            LineItem item = new LineItem(resultSet.getString("name"), resultSet.getInt("id"),
                    resultSet.getInt("quantity"), resultSet.getDouble("default_price"),
                    resultSet.getString("currency_type"));
            orderItems.add(item);
        }
        return orderItems;
    }
}
