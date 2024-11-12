package ru.gpncr.http.server.processors;


import ru.gpncr.http.server.BadRequestException;
import ru.gpncr.http.server.ExceptionHandler;
import ru.gpncr.http.server.HttpRequest;
import ru.gpncr.http.server.SendMessage;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class CalculatorProcessor implements RequestProcessor {
    private static final String CONTENT_TYPE = "text/html";
    private static final String STATUS_CODE_200 = "200 OK";
    @Override
    public void execute(HttpRequest request, OutputStream output) throws IOException {
        if (!request.containsParameter("a")) {
            throw new ExceptionHandler(400,"Parameter 'a' is missing");
        }
        if (!request.containsParameter("b")) {
            throw new ExceptionHandler(400,"Parameter 'b' is missing");
        }
        int a, b;
        try {
            a = Integer.parseInt(request.getParameter("a"));
        } catch (NumberFormatException e) {
            throw new ExceptionHandler(400,"Parameter 'a' has incorrect type");
        }
        try {
            b = Integer.parseInt(request.getParameter("b"));
        } catch (NumberFormatException e) {
            throw new ExceptionHandler(400,"Parameter 'b' has incorrect type");
        }

        String message = a + " + " + b + " = " + (a + b);

        SendMessage response = new SendMessage(CONTENT_TYPE, message, STATUS_CODE_200);
        response.send(output);
    }
}
