package com.codecool.shop.dao.implementation.jdbc;

import com.codecool.shop.dao.DatabaseManager;
import com.codecool.shop.dao.contracts.CartDao;
import com.codecool.shop.model.LineItem;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartDaoJdbc implements CartDao {

    private static CartDaoJdbc instance = null;
    DataSource dataSource;

    private CartDaoJdbc() {
        DatabaseManager dbManager = DatabaseManager.getInstance();
        dataSource = dbManager.getDataSource();
    }

    private CartDaoJdbc(String configFileName) {
        DatabaseManager dbManager = DatabaseManager.getInstance(configFileName);
        dataSource = dbManager.getDataSource();
    }

    public static CartDaoJdbc getInstance() {
        if (instance == null) {
            instance = new CartDaoJdbc();
        }
        return instance;
    }

    public static CartDaoJdbc getInstance(String configFileName) {
        if (instance == null) {
            instance = new CartDaoJdbc(configFileName);
        }
        return instance;
    }

    @Override
    public void add(LineItem lineItem) {


    }

    @Override
    public void add(LineItem lineItem, int userId) {
        int cartId = hasUserSavedCart(userId) ? getUserCartId(userId) : addCart(userId);
        try (Connection conn = dataSource.getConnection()) {
            String sql = "INSERT INTO cart_items (cart_id, product_id, quantity) VALUES (?,?,?) ";
            PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, cartId);
            statement.setInt(2, lineItem.getProductId());
            statement.setInt(3, lineItem.getQuantity());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public LineItem find(int id) {
        return null;
    }

    @Override
    public void remove(int id) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "DELETE FROM cart_items WHERE product_id = ?";
            PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<LineItem> getAll() {
        return null;
    }

    @Override
    public int getTotalQuantityInCart() {
        return 0;
    }

    @Override
    public double getTotalAmountInCart() {
        return 0;
    }

    @Override
    public void addAll(List<LineItem> list, int userId) {
        if (hasUserSavedCart(userId)) {
            for (LineItem item : list) {
                if (isInCart(item)) {
                    update(item, userId);
                } else {
                    add(item, userId);
                }
            }
        } else {
            list.forEach(lineItem -> add(lineItem, userId));
        }
    }

    private boolean isInCart(LineItem item) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT CASE WHEN EXISTS (SELECT * FROM cart_items WHERE product_id = ?) THEN 'TRUE' ELSE 'FALSE' END";
            PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, item.getProductId());
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getString(1).equals("TRUE");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void update(LineItem lineItem, int userId) {
        int cartId = getUserCartId(userId);
        try (Connection conn = dataSource.getConnection()) {
            String sql = "UPDATE cart_items SET quantity = ? WHERE cart_id = ? AND product_id = ?";
            PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, lineItem.getQuantity());
            statement.setInt(2, cartId);
            statement.setInt(3, lineItem.getProductId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int addCart(int userId) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "INSERT INTO cart (user_id) VALUES (?) RETURNING id ";
            PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, userId);
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            resultSet.next();
            return resultSet.getInt("id");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean hasUserSavedCart(int id) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT CASE WHEN EXISTS (SELECT * FROM cart WHERE user_id = ?) THEN 'TRUE' ELSE 'FALSE' END";
            PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getString(1).equals("TRUE");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<LineItem> getUserCart(int userId) {
        int cartId = getUserCartId(userId);
        try (Connection conn = dataSource.getConnection()) {
            String sql =
                    "SELECT product.id, product.name, product.default_price, currency.currency_type, cart_items.quantity FROM cart_items\n" +
                            "JOIN cart ON cart_items.cart_id = cart.id\n" +
                            "JOIN product on product.id = cart_items.product_id\n" +
                            "JOIN currency on product.currency_id = currency.id\n" +
                            "WHERE cart_id = ?\n" +
                            "AND user_id = ?";
            PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, cartId);
            statement.setInt(2, userId);
            ResultSet resultSet = statement.executeQuery();
            return getCartItems(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<LineItem> getCartItems(ResultSet resultSet) throws SQLException {
        List<LineItem> lineItems = new ArrayList<>();
        while (resultSet.next()) {
            LineItem item = new LineItem(resultSet.getString("name"),
                    resultSet.getInt("id"),
                    resultSet.getInt("quantity"),
                    resultSet.getDouble("default_price"),
                    resultSet.getString("currency_type"));
            lineItems.add(item);
        }
        return lineItems;
    }

    private int getUserCartId(int userId) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT id FROM cart WHERE user_id = ?";
            PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt("id");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeUsersCart(int id) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "DELETE FROM cart WHERE cart.user_id = ?";
            PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
