package ru.gpncr.http.server.processors;

import com.google.gson.Gson;
import ru.gpncr.http.server.ExceptionHandler;
import ru.gpncr.http.server.HttpRequest;
import ru.gpncr.http.server.SendMessage;
import ru.gpncr.http.server.app.DBStore;
import ru.gpncr.http.server.app.Product;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class GetProductByIdProcessor implements RequestProcessor {
    private static final String CONTENT_TYPE = "application/json";
    private static final String STATUS_CODE_200 = "200 OK";

    private DBStore DBStore;
    private int id;

    public GetProductByIdProcessor(DBStore DBStore, int id) {
        this.DBStore = DBStore;
        this.id = id;
    }

    @Override
    public void execute(HttpRequest request, OutputStream output) throws IOException {

        if(!request.getAcceptHeader().contains(CONTENT_TYPE)){
            throw new ExceptionHandler(406, "Not Acceptable. ERROR: The specified format type is not supported.");
        }

        Product products = DBStore.getProductById(id);
        String message = "{}";
        if(products != null){
            Gson gson = new Gson();
            message = gson.toJson(products);
        }
        SendMessage response = new SendMessage(CONTENT_TYPE, message, STATUS_CODE_200);
        response.send(output);
    }
}
