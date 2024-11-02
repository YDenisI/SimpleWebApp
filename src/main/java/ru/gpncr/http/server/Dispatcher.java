package ru.gpncr.http.server;


import ru.gpncr.http.server.app.ItemsRepository;
import ru.gpncr.http.server.processors.*;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Dispatcher {
    private Map<String, RequestProcessor> processors;
    private RequestProcessor defaultNotFoundProcessor;
    private RequestProcessor defaultInternalServerErrorProcessor;
    private RequestProcessor defaultBadRequestProcessor;
    private RequestProcessor getDefaultNotAllowedProcessor;

    private ItemsRepository itemsRepository;

    public Dispatcher() {
        this.itemsRepository = new ItemsRepository();
        this.processors = new HashMap<>();
        this.processors.put("GET /", new HelloWorldProcessor());
        this.processors.put("GET /calculator", new CalculatorProcessor());
        this.processors.put("GET /items", new GetAllItemsProcessor(itemsRepository));
        this.processors.put("POST /items", new CreateNewItemsProcessor(itemsRepository));
        this.defaultNotFoundProcessor = new DefaultNotFoundProcessor();
        this.defaultInternalServerErrorProcessor = new DefaultInternalServerErrorProcessor();
        this.defaultBadRequestProcessor = new DefaultBadRequestProcessor();
        this.getDefaultNotAllowedProcessor = new DefaultNotAllowedProcessor();
    }

    public void execute(HttpRequest request, OutputStream out) throws IOException {
        try {

            if(allowUri(request) && !allowMethod(request)){
                getDefaultNotAllowedProcessor.execute(request,out);
                return;
            }

            if (!processors.containsKey(request.getRoutingKey())) {
                defaultNotFoundProcessor.execute(request, out);
                return;
            }
            processors.get(request.getRoutingKey()).execute(request, out);
        } catch (BadRequestException e) {
            request.setException(e);
            defaultBadRequestProcessor.execute(request, out);
        } catch (Exception e) {
            e.printStackTrace();
            defaultInternalServerErrorProcessor.execute(request, out);
        }
    }

    public boolean allowUri(HttpRequest request){

        for (String key:processors.keySet()){
            String[] splitKey = key.split(" ");
            if(splitKey[1].equalsIgnoreCase(request.getUri())){
                    return true;
            }
        }
        return false;
    }

    public boolean allowMethod(HttpRequest request){

        for (String key:processors.keySet()){
            String[] splitKey = key.split(" ");
            if(splitKey[1].equalsIgnoreCase(request.getUri()) && splitKey[0].equalsIgnoreCase(request.getMethod().toString())){
                return true;
            }
        }
        return false;
    }

}
