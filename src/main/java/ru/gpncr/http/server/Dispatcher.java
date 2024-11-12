package ru.gpncr.http.server;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.gpncr.http.server.app.DBStore;
import ru.gpncr.http.server.processors.*;

import java.io.*;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Dispatcher {

    private static final Logger log = LogManager.getLogger(Dispatcher.class);
    private static final String regexIdNumber = "^/items/(\\d+)";
    private static final String regexCategoryIdNumber = "^/items/productsbycategory/(\\d+)";
    private static final String regexFILENAME = "/\\[(.*?)\\]";
    private static final String STATIC_DIR = "./static/";

  //  private static final String regexCategoryIdNumber = "^/items/(\\d+)/productsbycategory/(\\d+)";

    private Map<String, RequestProcessor> processors;
    private RequestProcessor defaultNotFoundProcessor;
    private RequestProcessor defaultInternalServerErrorProcessor;
    private RequestProcessor defaultBadRequestProcessor;
    private RequestProcessor getDefaultNotAllowedProcessor;
    private RequestProcessor exceptionHandleProcessor;

    private DBStore DBStore;
    private String uriPattern;
    private Set<String> allowUri;

    public String getUriPattern() {
        return uriPattern;
    }

    public Dispatcher()  {
        this.DBStore = new DBStore();
        this.processors = new HashMap<>();
        this.allowUri = new HashSet<>();
        this.allowUri.add("/");
        this.allowUri.add("/items");
        this.allowUri.add("/calculator");
        this.processors.put("GET /", new HelloWorldProcessor());
        this.processors.put("GET /calculator", new CalculatorProcessor());
        this.processors.put("GET /items", new GetAllProductsProcessor(DBStore));
        this.processors.put("POST /items", new CreateNewProductsProcessor(DBStore));
        this.processors.put("PUT /items", new UpdateProductsProcessor(DBStore));
        this.defaultNotFoundProcessor = new DefaultNotFoundProcessor();
        this.defaultInternalServerErrorProcessor = new DefaultInternalServerErrorProcessor();
        this.defaultBadRequestProcessor = new DefaultBadRequestProcessor();
        this.getDefaultNotAllowedProcessor = new DefaultNotAllowedProcessor();
    }

    public void execute(HttpRequest request, OutputStream out) throws IOException {
        try {
            if(processors.containsKey(request.getRoutingKey())){
                processors.get(request.getRoutingKey()).execute(request, out);
                return;
            }
            switch (request.getMethod()){
                case GET:
                    if(handleGet(request, out)) return;
                case DELETE:
                    if(handleDelete(request,out)) return;
            }

            if(allowUri.contains(request.getUri()) && !processors.containsKey(request.getRoutingKey())){
                getDefaultNotAllowedProcessor.execute(request,out);
                return;
            }

            defaultNotFoundProcessor.execute(request, out);
        } catch (ExceptionHandler e) {
            new ExceptionHandleProcessor(e.getStatusCode(), e.getMessage()).execute(request, out);
        } catch (Exception e) {
            e.printStackTrace();
            new ExceptionHandleProcessor(500, "Internal Server Error\n"+e.getMessage()).execute(request, out);
        }
    }

    private boolean handleGet(HttpRequest request, OutputStream out) throws SQLException, IOException{

        Pattern pattern = Pattern.compile(regexIdNumber);
        Matcher matcher = pattern.matcher(request.getUri());

        if (matcher.matches()) {
            int id = Integer.parseInt(matcher.group(1));
            new GetProductByIdProcessor(DBStore,id).execute(request, out);
            return true;
        }
        pattern = Pattern.compile(regexCategoryIdNumber);
        matcher = pattern.matcher(request.getUri());
        if (matcher.matches()) {
            int id = Integer.parseInt(matcher.group(1));
            new GetProductsByCategoryIdProcessor(DBStore,id).execute(request, out);
            return true;
        }

        if(getFile(request, out)) {
            return true;
        }

        return false;
    }

    private boolean handleDelete(HttpRequest request, OutputStream out) throws SQLException, IOException{

        Pattern pattern = Pattern.compile(regexIdNumber);
        Matcher matcher = pattern.matcher(request.getUri());

        if (matcher.matches()) {
            int id = Integer.parseInt(matcher.group(1));
            new DeleteProductsProcessor(DBStore,id).execute(request, out);
            return true;
        }
        return false;
    }

    private boolean getFile(HttpRequest request, OutputStream out) throws IOException, ExceptionHandler{

        if(request.getUri().split("/").length !=2) return false;

        Pattern pattern = Pattern.compile(regexFILENAME);
        Matcher matcher = pattern.matcher(request.getUri());
         String fileName = "";
        if (matcher.find()) {
               fileName = matcher.group(1);
        }else{
               return false;
        }

        File file = new File(STATIC_DIR, fileName);
        if (!file.exists() || file.isDirectory()) {
             throw new ExceptionHandler(400, "Bad Request. \n File not found");
        }
        String mimeType = Files.probeContentType(file.toPath());
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] content = fis.readAllBytes();
            new GetFileProcessor(content, mimeType).execute(request, out);
            return true;
        }
    }
}
