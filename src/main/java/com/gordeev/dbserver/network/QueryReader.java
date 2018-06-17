package com.gordeev.dbserver.network;

import com.gordeev.dbserver.entity.Query;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

public class QueryReader implements AutoCloseable{
    private InputStream inputStream;
    private final static ObjectMapper mapper = new ObjectMapper();

    public QueryReader(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public Query readQuery() {
        Query query = new Query();
        mapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
        try {
            query = mapper.readValue(inputStream, Query.class);
            query.setValid(true); //TODO:validate query later
        } catch (IOException e) {
            e.printStackTrace();
            query.setValid(false);
        }
        return query;
    }

    @Override
    public void close() throws Exception {
        inputStream.close();
    }
}
