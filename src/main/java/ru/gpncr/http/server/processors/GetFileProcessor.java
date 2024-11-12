package ru.gpncr.http.server.processors;

import ru.gpncr.http.server.ExceptionHandler;
import ru.gpncr.http.server.HttpRequest;
import ru.gpncr.http.server.SendMessage;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class GetFileProcessor implements RequestProcessor{

    private static final String STATUS_CODE_200 = "200 OK";

    private byte[] content;
    private String mimeType;

    public GetFileProcessor(byte[] content, String mimeType) {
        this.content = content;
        this.mimeType = mimeType;
    }

    @Override
    public void execute(HttpRequest request, OutputStream output) throws IOException, ExceptionHandler {

        SendMessage response = new SendMessage(mimeType, new String(content), STATUS_CODE_200);
        response.send(output);

    }
}
