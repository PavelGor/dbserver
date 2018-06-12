package com.gordeev.dbserver.security.entity;

import com.gordeev.dbserver.network.QueryReader;
import com.gordeev.dbserver.network.ResultWriter;

public class User {
    private String login;
    private String password;
    private QueryReader queryReader;
    private ResultWriter resultWriter;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public QueryReader getQueryReader() {
        return queryReader;
    }

    public void setQueryReader(QueryReader queryReader) {
        this.queryReader = queryReader;
    }

    public ResultWriter getResultWriter() {
        return resultWriter;
    }

    public void setResultWriter(ResultWriter resultWriter) {
        this.resultWriter = resultWriter;
    }

    @Override
    public String toString() {
        return "User{" +
                "login='" + login + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
