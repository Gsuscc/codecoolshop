package com.codecool.shop.dao.implementation.jdbc;

import com.codecool.shop.dao.DatabaseManager;
import com.codecool.shop.dao.contracts.ProductDao;
import com.codecool.shop.model.Product;
import com.codecool.shop.model.ProductCategory;
import com.codecool.shop.model.Supplier;
import com.codecool.shop.util.ResultSetHelper;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDaoJdbc implements ProductDao {

    private static ProductDaoJdbc instance = null;
    DataSource dataSource;

    private ProductDaoJdbc() {
        DatabaseManager dbManager = DatabaseManager.getInstance();
        dataSource = dbManager.getDataSource();
    }

    private ProductDaoJdbc(String configFileName) {
        DatabaseManager dbManager = DatabaseManager.getInstance(configFileName);
        dataSource = dbManager.getDataSource();
    }

    public static ProductDaoJdbc getInstance() {
        if (instance == null) {
            instance = new ProductDaoJdbc();
        }
        return instance;
    }

    public static ProductDaoJdbc getInstance(String configFileName) {
        if (instance == null) {
            instance = new ProductDaoJdbc(configFileName);
        }
        return instance;
    }


    private int getCurrencyId(String currency) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT * FROM currency WHERE currency_type = (?)";
            PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, currency);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) return 0;
            return resultSet.getInt("id");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void add(Product product) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "INSERT INTO product (name, description, default_price, currency_id, product_category_id, supplier_id) VALUES (?,?,?,?,?,?)";
            PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, product.getName());
            statement.setString(2, product.getDescription());
            statement.setDouble(3, product.getDefaultPrice());
            statement.setInt(4, getCurrencyId(product.getDefaultCurrency().toString()));
            statement.setInt(5, product.getProductCategory().getId());
            statement.setInt(6, product.getSupplier().getId());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Product find(int id) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT * FROM product p JOIN currency c on p.currency_id = c.id\n" +
                    "JOIN product_category pc on p.product_category_id = pc.id\n" +
                    "JOIN supplier s on p.supplier_id = s.id\n" +
                    "WHERE p.id = ?";
            PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            return buildProducts(new ResultSetHelper(resultSet)).get(0);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(int id) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "DELETE FROM product WHERE id= ?";
            PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setLong(1, id);
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Product> getAll() {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT * FROM product p JOIN currency c on p.currency_id = c.id\n" +
                    "JOIN product_category pc on p.product_category_id = pc.id\n" +
                    "JOIN supplier s on p.supplier_id = s.id";
            PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ResultSet resultSet = statement.executeQuery();
            return buildProducts(new ResultSetHelper(resultSet));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Product> buildProducts(ResultSetHelper helper) throws SQLException {
        List<Product> products = new ArrayList<>();
        ResultSet resultSet = helper.getResultSet();
        while (resultSet.next()) {
            Product productToAdd = new Product(
                    resultSet.getString(helper.getColumnIndex("product.name")),
                    Double.parseDouble(resultSet.getString(helper.getColumnIndex("product.default_price"))),
                    resultSet.getString(helper.getColumnIndex("currency.currency_type")),
                    resultSet.getString(helper.getColumnIndex("product.description")),
                    getCategory(helper, resultSet), getSupplier(helper, resultSet));
            productToAdd.setId(resultSet.getInt(helper.getColumnIndex("product.id")));
            products.add(productToAdd);
        }
        return products;
    }


    private Supplier getSupplier(ResultSetHelper helper, ResultSet resultSet) throws SQLException {
        Supplier supplier = new Supplier(resultSet.getString(helper.getColumnIndex("supplier.name")),
                resultSet.getString(helper.getColumnIndex("supplier.description")));
        supplier.setId(resultSet.getInt(helper.getColumnIndex("supplier.id")));
        return supplier;
    }


    private ProductCategory getCategory(ResultSetHelper helper, ResultSet resultSet) throws SQLException {
        ProductCategory category = new ProductCategory(resultSet.getString(helper.getColumnIndex("product_category.name")),
                resultSet.getString(helper.getColumnIndex("product_category.type")),
                resultSet.getString(helper.getColumnIndex("product_category.description")));
        category.setId(resultSet.getInt(helper.getColumnIndex("product_category.id")));
        return category;
    }

    @Override
    public List<Product> getBy(Supplier supplier) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT * FROM product p JOIN currency c on p.currency_id = c.id\n" +
                    "JOIN product_category pc on p.product_category_id = pc.id\n" +
                    "JOIN supplier s on p.supplier_id = s.id\n" +
                    "WHERE p.supplier_id = ?";
            PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, supplier.getId());
            ResultSet resultSet = statement.executeQuery();
            return buildProducts(new ResultSetHelper(resultSet));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Product> getBy(ProductCategory productCategory) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT * FROM product p JOIN currency c on p.currency_id = c.id\n" +
                    "JOIN product_category pc on p.product_category_id = pc.id\n" +
                    "JOIN supplier s on p.supplier_id = s.id\n" +
                    "WHERE p.product_category_id = ?";
            PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, productCategory.getId());
            ResultSet resultSet = statement.executeQuery();
            return buildProducts(new ResultSetHelper(resultSet));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
