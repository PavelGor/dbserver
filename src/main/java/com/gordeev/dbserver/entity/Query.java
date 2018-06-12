package com.gordeev.dbserver.entity;

import java.util.HashMap;
import java.util.Map;

public class Query {
    private QueryType type;
    private String dbName;
    private String tableName;
    private Map<String, String> columns = new HashMap<>();
    //columnNames, columnValues, conditions
    //JSON

    public QueryType getType() {
        return type;
    }

    public void setType(QueryType type) {
        this.type = type;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Map<String, String> getColumns() {
        return columns;
    }

    public void setColumns(Map<String, String> columns) {
        this.columns = columns;
    }
}
