package ru.gpncr.http.server.processors;

import ru.gpncr.http.server.HttpRequest;
import ru.gpncr.http.server.SendMessage;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class HelloWorldProcessor implements RequestProcessor {
    private static final String CONTENT_TYPE = "text/html";
    private static final String STATUS_CODE_200 = "200 OK";
    @Override
    public void execute(HttpRequest request, OutputStream output) throws IOException {

        String message = "Hello World!!!";
        SendMessage response = new SendMessage(CONTENT_TYPE, message, STATUS_CODE_200);
        response.send(output);

    }
}
