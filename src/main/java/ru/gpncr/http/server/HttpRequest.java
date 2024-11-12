package ru.gpncr.http.server;

import org.apache.logging.log4j.LogManager;

import java.math.BigDecimal;
import java.util.*;

public class HttpRequest {
    private static final org.apache.logging.log4j.Logger log = LogManager.getLogger(HttpRequest.class);
    private static final String ACCEPT = "Accept";

    private String rawRequest;
    private HttpMethod method;
    private String uri;
    private Map<String, String> parameters;
    private List<String> acceptHeader;
    String body;
    private Map<String, String> headers;

    public List<String> getAcceptHeader() {
        return acceptHeader;
    }

/*  public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }*/

    public HttpMethod getMethod() {
        return method;
    }

    public HttpRequest(String rawRequest) {
        this.rawRequest = rawRequest;
        this.headers = new HashMap<>();
        this.parse();
    }

    public String getUri() {
        return uri;
    }

    public String getParameter(String key) {
        return parameters.get(key);
    }

    public String getBody() {
        return body;
    }

    public boolean containsParameter(String key) {
        return parameters.containsKey(key);
    }
    public String getRoutingKey() {
        return method + " " + uri;
    }

    private void parse() {
        int startIndex = rawRequest.indexOf(' ');
        int endIndex = rawRequest.indexOf(' ', startIndex + 1);
        uri = rawRequest.substring(startIndex + 1, endIndex);
        if(uri.length() > 1 && uri.endsWith("/")){
            uri = uri.substring(0, uri.length() - 1);
        }
        method = HttpMethod.valueOf(rawRequest.substring(0, startIndex));

        getHeadersInfo();
        acceptHeader = new ArrayList<>(Arrays.asList(headers.get(ACCEPT).split(",")));
        if (acceptHeader.isEmpty()) {
            acceptHeader.add("/");
        }

        getRequestParam();

        if (method == HttpMethod.POST || method == HttpMethod.PUT) {
            this.body = rawRequest.substring(rawRequest.indexOf("\r\n\r\n") + 4);
        }
    }

    public void getRequestParam(){
            parameters = new HashMap<>();
            if (uri.contains("?")) {
                String[] elements = uri.split("[?]");
                uri = elements[0];
                String[] keysValue = elements[1].split("[&]");
                for (String o : keysValue) {
                    String[] keyValue = o.split("=");
                    parameters.put(keyValue[0], keyValue[1]);
                }
            }
    }

    public void getHeadersInfo(){
        String[] lines = rawRequest.split("\r\n");
        for (int i = 1; i < lines.length; i++) {
            String headerLine = lines[i];
            if (headerLine.isEmpty()) {
                break;
            }
            String[] header = headerLine.split(": ", 2);
            if (header.length == 2) {
                headers.put(header[0], header[1]);
            }
        }
    }

    public void info(boolean debug) {
       /* if (debug) {
            log.debug(rawRequest);
        }*/

        log.info("Method: " + method);
        log.info("Uri: " + uri);
        log.info("Parameter: " + parameters);
        log.info("Body: " + body);
        log.info("Header: " + headers);
        log.info("Accept: " + acceptHeader);
    }
}