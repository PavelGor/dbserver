package com.gordeev.dbserver.entity;

public enum QueryType {
    SELECT("select"), CREATE_DATABASE("create database"), CREATE_TABLE("create table"), DELETE("delete"), UPDATE("update"), INSERT("insert");

    private final String type;

    QueryType(String type) {
        this.type = type;
    }
}
