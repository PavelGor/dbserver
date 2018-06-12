package com.gordeev.dbserver.network;

import com.gordeev.dbserver.entity.Query;

import java.io.InputStream;

public class QueryReader {
    private InputStream inputStream;

    public QueryReader(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public Query readQuery() {
        return null;
    }

    public String readLogin() {
        return null;
    }

    public String readPassword() {
        return null;
    }
}
