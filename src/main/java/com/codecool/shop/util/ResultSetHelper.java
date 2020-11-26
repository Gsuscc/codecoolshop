package com.codecool.shop.util;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class ResultSetHelper {
    private final Map<String, Integer> columnMap = new LinkedHashMap<>();
    private final ResultSet resultSet;

    public ResultSetHelper(ResultSet resultSet) throws SQLException {
        this.resultSet = resultSet;
        ResultSetMetaData metaData = this.resultSet.getMetaData();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            String tableAlias = metaData.getTableName(i);
            String columnName = metaData.getColumnLabel(i);
            if (!columnMap.containsKey(columnName)) {
                columnMap.put(tableAlias + "." + columnName, i);
            }
        }
    }

    public int getColumnIndex(String columnName) {
        return columnMap.get(columnName);
    }

    public ResultSet getResultSet() {
        return resultSet;
    }

}
