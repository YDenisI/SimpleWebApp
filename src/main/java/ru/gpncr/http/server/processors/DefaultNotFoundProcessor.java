package ru.gpncr.http.server.processors;

import ru.gpncr.http.server.HttpRequest;
import ru.gpncr.http.server.SendMessage;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class DefaultNotFoundProcessor implements RequestProcessor {
    private static final String CONTENT_TYPE = "text/html";
    private static final String STATUS_CODE_404 = "404 Not Found";
    @Override
    public void execute(HttpRequest request, OutputStream output) throws IOException {

        String message = STATUS_CODE_404;
        SendMessage response = new SendMessage(CONTENT_TYPE, message, STATUS_CODE_404);
        response.send(output);
    }
}
