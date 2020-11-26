package com.codecool.shop.dao.implementation.jdbc;

import com.codecool.shop.dao.contracts.ProductCategoryDao;
import com.codecool.shop.dao.contracts.ProductDao;
import com.codecool.shop.dao.contracts.SupplierDao;
import com.codecool.shop.model.Product;
import com.codecool.shop.model.ProductCategory;
import com.codecool.shop.model.Supplier;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductDaoJdbcTest {

    private static ProductDao productDao;
    private static ProductCategoryDao productCategoryDao;
    private static SupplierDao supplierDao;
    private Product product1;
    private Product product2;
    private ProductCategory category;
    private Supplier supplier;


    @BeforeEach
    void setUp() {
        productDao = ProductDaoJdbc.getInstance("test_connection.properties");
        productCategoryDao = ProductCategoryDaoJdbc.getInstance("test_connection.properties");
        supplierDao = SupplierDaoJdbc.getInstance("test_connection.properties");
        category = new ProductCategory("pc1", "Hardware", "desc-pc1");
        supplier =  new Supplier("s1", "description-s1");
        category.setId(1);
        supplier.setId(1);
        product1 = new Product("Test prod1", 100, "USD", "description", category, supplier );
        product2 = new Product("Test prod2", 200, "USD", "description2", category, supplier);
        addTestCurrency();
        productCategoryDao.add(category);
        supplierDao.add(supplier);

    }

    @Test
    @Order(1)
    void add_Product_addProductObjectWithIdOneToProductTable() {
        int id = 1;
        productDao.add(product1);
        Product product = productDao.find(id);
        String productName  = product.getName();
        double defaultPrice = product.getDefaultPrice();
        String currencyString = product.getDefaultCurrency().toString();
        String description = product.getDescription();
        String categoryName = product.getProductCategory().getName();
        String supplierName = product.getSupplier().getName();
        assertEquals(product1.getName(), productName);
        assertEquals(product1.getDefaultPrice(), defaultPrice);
        assertEquals(product1.getDescription(), description);
        assertEquals(product1.getDefaultCurrency().toString(), currencyString);
        assertEquals(product1.getProductCategory().getName(), categoryName);
        assertEquals(product1.getSupplier().getName(), supplierName);
    }

    @Test
    @Order(2)
    void find_idTwo_returnsProductObject() {
        int id = 2;
        product2.setId(id);
        productDao.add(product2);
        Product product = productDao.find(id);
        assertEquals(product2.getId(), product.getId());
        assertEquals(product2.getName(), product.getName());
        assertEquals(product2.getDefaultPrice(), product.getDefaultPrice());
        assertEquals(product2.getDescription(), product.getDescription());
        assertEquals(product2.getDefaultCurrency(), product.getDefaultCurrency());
        assertEquals(product2.getProductCategory().getName(), product.getProductCategory().getName());
        assertEquals(product2.getSupplier().getName(), product.getSupplier().getName());
    }

    @Test
    @Order(3)
    void getAll_tableNotEmpty_returnsListOfAllProduct() {
        List<Product> productCategories = productDao.getAll();
        assertEquals(2, productCategories.size());
    }

    @Test
    @Order(4)
    void find_idZero_throwIllegalArgumentException() {
        assertThrows(IndexOutOfBoundsException.class,()-> productDao.find(0));
    }

    @Test
    @Order(5)
    void find_idOutOfRange_throwIllegalArgumentException() {
        assertThrows(IndexOutOfBoundsException.class,()-> productDao.find(100));
    }

    @Test
    @Order(6)
    void remove_idOne_removeRecord() {
        assertDoesNotThrow(() -> productDao.remove(1));
    }



    private void addTestCurrency(){
        String type = "USD";
        try (Connection conn = ((ProductDaoJdbc)productDao).dataSource.getConnection()) {
            String sql = "INSERT INTO currency (currency_type) VALUES (?)";
            PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, type);
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

