package com.gordeev.dbserver.network;

import com.gordeev.dbserver.entity.Query;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public class QueryReader implements AutoCloseable{
    private final static ObjectMapper mapper = new ObjectMapper();
    private static final Logger LOG = LoggerFactory.getLogger(QueryReader.class);
    private InputStream inputStream;

    public QueryReader(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public Query readQuery() {
        Query query = new Query();
        mapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
        try {
            query = mapper.readValue(inputStream, Query.class);
            query.setValid(true); //TODO:validate query later
            LOG.info("Received request-query from client: {}", query);
        } catch (IOException e) {
            query.setValid(false);
            LOG.info("Mapper cannot read query from client");
        }
        return query;
    }

    @Override
    public void close() throws Exception {
        inputStream.close();
    }
}
