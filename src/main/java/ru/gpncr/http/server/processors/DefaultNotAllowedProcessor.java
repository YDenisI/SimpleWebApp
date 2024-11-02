package ru.gpncr.http.server.processors;

import ru.gpncr.http.server.HttpRequest;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class DefaultNotAllowedProcessor implements RequestProcessor {
    @Override
    public void execute(HttpRequest request, OutputStream output) throws IOException {
        String response = "" +
                "HTTP/1.1 405 Not Allowed\r\n" +
                "Content-Type: text/html\r\n" +
                "\r\n" +
                "<html><body><h1>405 Method Not Allowed. Недопустимый метод</h1></body></html>";
        output.write(response.getBytes(StandardCharsets.UTF_8));
    }
}
