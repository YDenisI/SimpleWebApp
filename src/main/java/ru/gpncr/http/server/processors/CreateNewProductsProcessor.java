package ru.gpncr.http.server.processors;

import com.google.gson.Gson;
import ru.gpncr.http.server.ExceptionHandler;
import ru.gpncr.http.server.HttpRequest;
import ru.gpncr.http.server.SendMessage;
import ru.gpncr.http.server.app.Product;
import ru.gpncr.http.server.app.DBStore;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class CreateNewProductsProcessor implements RequestProcessor {
    private static final String CONTENT_TYPE = "text/html";
    private static final String STATUS_CODE_200 = "200 OK";

    private DBStore DBStore;

    public CreateNewProductsProcessor(DBStore DBStore) {
        this.DBStore = DBStore;
    }

    @Override
    public void execute(HttpRequest request, OutputStream output) throws IOException {

        if(!request.getAcceptHeader().contains(CONTENT_TYPE)){
            throw new ExceptionHandler(406, "Not Acceptable. ERROR: The specified format type is not supported.");
        }

        Gson gson = new Gson();
        DBStore.save(gson.fromJson(request.getBody(), Product.class));

        String message = "Product create";

        SendMessage response = new SendMessage(CONTENT_TYPE, message, STATUS_CODE_200);
        response.send(output);

    }
}
