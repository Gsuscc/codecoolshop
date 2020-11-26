package com.codecool.shop.dao.implementation.jdbc;

import com.codecool.shop.dao.contracts.ProductCategoryDao;
import com.codecool.shop.model.ProductCategory;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductCategoryDaoJdbcTest {

    private static ProductCategoryDao productCategoryDao;

    @BeforeAll
    static void setup() {
        productCategoryDao = ProductCategoryDaoJdbc.getInstance("test_connection.properties");
    }

    @Test
    @Order(1)
    void add_Object_addProductCategoryObjectWithIdOneToProductCategoryTable() {
        String name = "Test ProdCat1";
        String department = "Hardware";
        String description = "Description of ProdCat1";
        ProductCategory productCategory = new ProductCategory(name, department, description);
        productCategoryDao.add(productCategory);
        String categoryName = productCategoryDao.find(1).getName();
        String categoryDepartment = productCategoryDao.find(1).getDepartment();
        String categoryDescription = productCategoryDao.find(1).getDescription();
        assertEquals(name, categoryName);
        assertEquals(department, categoryDepartment);
        assertEquals(description, categoryDescription);
    }

    @Test
    @Order(2)
    void find_idOne_returnsProductCategoryObject() {
        int id = 1;
        String name = "Test ProdCat1";
        String department = "Hardware";
        String description = "Description of ProdCat1";
        ProductCategory productCategory = productCategoryDao.find(1);
        assertEquals(id, productCategory.getId());
        assertEquals(name, productCategory.getName());
        assertEquals(department, productCategory.getDepartment());
        assertEquals(description, productCategory.getDescription());
    }

    @Test
    @Order(3)
    void getAll_tableNotEmpty_returnsListOfAllProductCategory() {
        productCategoryDao.add(new ProductCategory("pc1", "Hardware" ,"nice"));
        productCategoryDao.add(new ProductCategory("pc2","Hardware","ugly"));
        List<ProductCategory> productCategorys = productCategoryDao.getAll();
        assertEquals(3,productCategorys.size());
    }

    @Test
    @Order(4)
    void find_idZero_returnNull() {
        assertNull(productCategoryDao.find(0));
    }

    @Test
    @Order(5)
    void remove_idOne_removeRecord() {
        assertDoesNotThrow(()->productCategoryDao.remove(1));
    }
}