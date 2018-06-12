package com.gordeev.dbserver.entity;

public enum QueryType {
    SELECT("select"), CREATEDATABASE("create database"), CREATETABLE("create table"), DELETE("delete"), UPDATE("update"), ERROR("error");

    private final String type;

    QueryType(String type) {
        this.type = type;
    }

    public static QueryType getByName(String type) {
        for(QueryType queryType : values()){
            if (queryType.type.equalsIgnoreCase(type)){
                return queryType;
            }
        }
        throw new IllegalArgumentException("No query type with type " + type + " found");
    }
}
