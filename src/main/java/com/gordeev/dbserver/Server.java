package com.gordeev.dbserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private int port;
    private Object MONITOR = new Object();

    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);

        while (true) {
            Socket socket = serverSocket.accept();
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();

            Handler handler = new Handler(MONITOR);
            handler.setInputStream(inputStream);
            handler.setOutputStream(outputStream);

            Thread thread = new Thread(handler);
            thread.start();
        }
    }

    public void setPort(int port) {
        this.port = port;
    }
}
