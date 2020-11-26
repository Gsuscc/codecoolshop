package com.codecool.shop.dao.implementation.jdbc;

import com.codecool.shop.dao.contracts.SupplierDao;
import com.codecool.shop.model.Supplier;

import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SupplierDaoJdbcTest {

    private static SupplierDao supplierDao;

    @BeforeAll
    static void setup() {
        supplierDao = SupplierDaoJdbc.getInstance("test_connection.properties");
    }


    @Test
    @Order(1)
    void add_SupplierObject_addSupplierObjectWithIdOneToSupplierTable() {
        String name = "Test Supplier1";
        String description = "Description of Supplier1";
        Supplier supplier = new Supplier(name, description);
        supplierDao.add(supplier);
        String supplierName = supplierDao.find(1).getName();
        String supplierDescription = supplierDao.find(1).getDescription();
        assertEquals(name, supplierName);
        assertEquals(description, supplierDescription);
    }

    @Test
    @Order(2)
    void find_idOne_returnsSupplierObject() {
        int id = 1;
        String name = "Test Supplier1";
        String description = "Description of Supplier1";
        Supplier supplier = supplierDao.find(1);
        assertEquals(id, supplier.getId());
        assertEquals(name, supplier.getName());
        assertEquals(description, supplier.getDescription());
    }

    @Test
    @Order(3)
    void getAll_tableNotEmpty_returnsListOfAllSupplier() {
        supplierDao.add(new Supplier("sup1", "nice"));
        supplierDao.add(new Supplier("sup2","ugly"));
        List<Supplier> suppliers = supplierDao.getAll();
        assertEquals(3,suppliers.size());
    }

    @Test
    @Order(4)
    void find_idZero_returnNull() {
        assertNull(supplierDao.find(0));
    }

    @Test
    @Order(5)
    void remove_idOne_removeRecord() {
        assertDoesNotThrow(()->supplierDao.remove(1));
    }
}