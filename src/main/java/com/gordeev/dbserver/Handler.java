package com.gordeev.dbserver;

import com.gordeev.dbserver.db.DbHandler;
import com.gordeev.dbserver.entity.Query;
import com.gordeev.dbserver.entity.QueryType;
import com.gordeev.dbserver.entity.Result;
import com.gordeev.dbserver.entity.ResultType;
import com.gordeev.dbserver.network.QueryReader;
import com.gordeev.dbserver.network.ResultWriter;
import com.gordeev.dbserver.security.Security;
import com.gordeev.dbserver.security.entity.User;

import java.io.*;

public class Handler implements Runnable {
    private InputStream inputStream;
    private OutputStream outputStream;
    private final Object MONITOR;

    Handler(Object monitor) {
        this.MONITOR = monitor;
    }

    @Override
    public void run() {
        QueryReader queryReader = new QueryReader(inputStream);
        ResultWriter resultWriter = new ResultWriter(outputStream);

        String login = queryReader.readLogin();
        String password = queryReader.readPassword();
        User user = new User();
        if (login != null || password != null) {
            user.setLogin(login);
            user.setPassword(password);
            user.setQueryReader(queryReader);
            user.setResultWriter(resultWriter);
        }
        if (!Security.checkUser(user)) {
            resultWriter.write(new Result(ResultType.ERROR, "no such user in database, try again later."));
            try {
                outputStream.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            throw new RuntimeException("no such user in database");
        }

        Query query = queryReader.readQuery();
        DbHandler dbHandler = new DbHandler();

        while ((query != null) && (!Thread.interrupted())) {
            if (query.getType() == QueryType.ERROR) {
                resultWriter.write(new Result(ResultType.ERROR, "no such Query can be used in database, try again."));
                break;
            }
            Result result;
            synchronized (MONITOR) {
                result = dbHandler.executeQuery(query);
            }
            resultWriter.write(result);
            query = queryReader.readQuery();
        }

        try {
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }
}
