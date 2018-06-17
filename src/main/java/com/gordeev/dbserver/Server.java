package com.gordeev.dbserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private int port;
    private static final Logger LOG = LoggerFactory.getLogger(Server.class);

    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        LOG.info("Server started and now listen port: {}", port);
        while (true) {
            Socket socket = serverSocket.accept();
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();

            ConnectionHandler connectionHandler = new ConnectionHandler();
            connectionHandler.setInputStream(inputStream);
            connectionHandler.setOutputStream(outputStream);

            Thread thread = new Thread(connectionHandler);
            thread.start();
            LOG.info("Received new connection to DB and started new thread for it");
        }
    }

    public void setPort(int port) {
        this.port = port;
    }
}
