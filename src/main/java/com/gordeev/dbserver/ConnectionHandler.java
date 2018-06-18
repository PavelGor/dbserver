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

            while (query != null) {
                if (!query.isValid()) {
                    resultWriter.write(new Result(ResultType.ERROR, "no such Query can be used in database, try again."));
                    break;
                }
                Result result = DB_HANDLER.executeQuery(query);
                resultWriter.write(result);
                query = queryReader.readQuery();
            }
        } catch (Exception e) {
            LOG.info("Something wrong with client connection: {}", e);
        }
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }
}
