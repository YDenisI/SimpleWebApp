package ru.gpncr.http.server.processors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.gpncr.http.server.HttpRequest;
import ru.gpncr.http.server.SendMessage;

import java.io.IOException;
import java.io.OutputStream;

public class ExceptionHandleProcessor implements RequestProcessor{
    private static final String CONTENT_TYPE = "text/html";

    private int statusCode;
    private String message;
    private static final Logger log = LogManager.getLogger(ExceptionHandleProcessor.class);
    public ExceptionHandleProcessor(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    @Override
    public void execute(HttpRequest request, OutputStream output) throws IOException {
        String statusText = "200 OK";
        switch (statusCode){
            case 400:
                statusText = "400 Bad Request";
                break;
            case 500:
                statusText = "500 Internal Server Error";
                break;
            case 404:
                statusText = "404 Not Found";
                break;
            case 406:
                statusText ="406 Not Acceptable";
                break;
        }

        SendMessage response = new SendMessage(CONTENT_TYPE, message, statusText);
        response.send(output);
    }
}
