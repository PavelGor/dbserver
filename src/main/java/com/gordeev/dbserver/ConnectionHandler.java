package com.gordeev.dbserver;

import com.gordeev.dbserver.db.DbHandler;
import com.gordeev.dbserver.entity.Query;
import com.gordeev.dbserver.entity.Result;
import com.gordeev.dbserver.entity.ResultType;
import com.gordeev.dbserver.network.QueryReader;
import com.gordeev.dbserver.network.ResultWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class ConnectionHandler implements Runnable {
    private final static DbHandler DB_HANDLER = new DbHandler();
    private static final Logger LOG = LoggerFactory.getLogger(ConnectionHandler.class);
    private InputStream inputStream;
    private OutputStream outputStream;

    @Override
    public void run() {
        try (QueryReader queryReader = new QueryReader(inputStream);
             ResultWriter resultWriter = new ResultWriter(outputStream)) {

            Query query = queryReader.readQuery();
            query.setValid(true); //TODO:validate query later
            LOG.info("Received request-query from client: {}", query);

            while (query != null) {
                if (!query.isValid()) {
                    resultWriter.write(new Result(ResultType.ERROR, "no such Query can be used in database, try again."));
                    break;
                }
                Result result = DB_HANDLER.executeQuery(query);
                resultWriter.write(result);
                LOG.info("Result of query was send to user: {}", result);
                query = queryReader.readQuery();
                LOG.info("Received new query from client: {}", query);
            }
        } catch (Exception e) {
            LOG.info("Something wrong with this client: {}", e);
        }
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }
}
