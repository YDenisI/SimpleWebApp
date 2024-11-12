package ru.gpncr.http.server.processors;

import com.google.gson.Gson;
import ru.gpncr.http.server.ExceptionHandler;
import ru.gpncr.http.server.HttpRequest;
import ru.gpncr.http.server.SendMessage;
import ru.gpncr.http.server.app.Product;
import ru.gpncr.http.server.app.DBStore;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class GetAllProductsProcessor implements RequestProcessor {
    private static final String CONTENT_TYPE = "application/json";
    private static final String STATUS_CODE_200 = "200 OK";

    private DBStore DBStore;

    public GetAllProductsProcessor(DBStore DBStore) {
        this.DBStore = DBStore;
    }

    @Override
    public void execute(HttpRequest request, OutputStream output) throws IOException {

        if(!request.getAcceptHeader().contains(CONTENT_TYPE)){
            throw new ExceptionHandler(406, "Not Acceptable. ERROR: The specified format type is not supported.");
        }

        List<Product> products = DBStore.getProducts();
        String message = "{}";
        if(!products.isEmpty()){
            Gson gson = new Gson();
            message = gson.toJson(products);
        }

        SendMessage response = new SendMessage(CONTENT_TYPE, message, STATUS_CODE_200);
        response.send(output);
    }
}
