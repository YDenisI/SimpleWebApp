package ru.gpncr.http.server;


import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.net.ServerSocket;

import java.net.Socket;
import java.sql.SQLException;

public class HttpServer {
    private static final org.apache.logging.log4j.Logger log = LogManager.getLogger(HttpServer.class);

    private int port;
    private Dispatcher dispatcher;

    public HttpServer(int port) {
        this.port = port;
        this.dispatcher = new Dispatcher();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            log.info("Server run port " + port);
            while (true) {
                try (Socket socket = serverSocket.accept()) {

                    byte[] buffer = new byte[8192];
                    int n = socket.getInputStream().read(buffer);
                    if (n < 1) {
                        continue;
                    }
                    String rawRequest = new String(buffer, 0, n);
                    //log.info("rawRequest "+rawRequest);
                    HttpRequest request = new HttpRequest(rawRequest);
                    request.info(true);
                    dispatcher.execute(request, socket.getOutputStream());
                }
            }
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
        }
    }
}
