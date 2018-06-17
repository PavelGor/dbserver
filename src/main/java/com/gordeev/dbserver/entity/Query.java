package com.gordeev.dbserver.entity;

import java.util.HashMap;
import java.util.Map;

public class Query {
    private QueryType type;
    private String dbName;
    private String tableName;
    private Map<String, String> columns =  new HashMap<>();
    private boolean valid;
    private Map<String,String> conditions = new HashMap<>();
    private Map<String,String> values = new HashMap<>();

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

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public boolean isValid() {
        return valid;
    }

    public Map<String, String> getConditions() {
        return conditions;
    }

    public void setConditions(Map<String, String> conditions) {
        this.conditions = conditions;
    }

    public Map<String,String> getValues() {
        return values;
    }

    @Override
    public String toString() {
        return "Query{" +
                "type=" + type +
                ", dbName='" + dbName + '\'' +
                ", tableName='" + tableName + '\'' +
                ", columns=" + columns +
                ", valid=" + valid +
                ", conditions=" + conditions +
                ", values=" + values +
                '}';
    }
}
