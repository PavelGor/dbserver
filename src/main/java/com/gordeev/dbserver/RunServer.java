package com.gordeev.dbserver;

import java.io.IOException;

public class RunServer {
    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.setPort(5318);
        server.start();
    }
}
