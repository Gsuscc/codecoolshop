package com.codecool.shop.dao;

import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;


public class DatabaseManager {
    private static DatabaseManager instance = null;
    private String configFileName = "connection.properties";
    private Properties dBProperties = new Properties();
    private DataSource dataSource;

    private DatabaseManager() {
        try {
            setup();
        } catch (SQLException | IOException ex) {
            System.out.println("Cannot connect to database.");
        }
    }

    private DatabaseManager(String configFileName) {
        this.configFileName = configFileName;
        try {
            setup();
        } catch (SQLException | IOException ex) {
            System.out.println("Cannot connect to database.");
        }
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /***
     *
     * @param configFileName
     * @return
     */
    public static DatabaseManager getInstance(String configFileName) {
        if (instance == null) {
            instance = new DatabaseManager(configFileName);
        }
        return instance;
    }

    public void setup() throws SQLException, IOException {
        setProperties();
        this.dataSource = connect();
    }

    private DataSource connect() throws SQLException {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        String dbName = dBProperties.getProperty("database");
        String user = dBProperties.getProperty("user");
        String password = dBProperties.getProperty("password");

        dataSource.setDatabaseName(dbName);
        dataSource.setUser(user);
        dataSource.setPassword(password);

        System.out.println("Trying to connect");
        dataSource.getConnection().close();
        System.out.println("Connection  ok to DataBase: " + dBProperties.getProperty("database"));

        return dataSource;
    }

    private void setProperties() throws IOException {
        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        String dBConfigPath = rootPath + configFileName;
        dBProperties.load(new FileInputStream(dBConfigPath));
    }

    public DataSource getDataSource() {
        return dataSource;
    }
}
