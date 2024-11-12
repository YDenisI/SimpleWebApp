package ru.gpncr.http.server;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class SendMessage {
    private static final String CHARSET = "charset=UTF-8";

    private String contentType;
    private String message;
    private String statusCode;

    public SendMessage(String contentType, String message, String statusCode) {
        this.contentType = contentType;
        this.message = message;
        this.statusCode = statusCode;
    }

    public void send(OutputStream output) throws IOException {
        String response = "" +
                "HTTP/1.1 "+statusCode+"\r\n" +
                "Content-Type: "+contentType+";"+CHARSET+"\r\n" +
                "\r\n" +
                message;
        output.write(response.getBytes(StandardCharsets.UTF_8));
    }
}
