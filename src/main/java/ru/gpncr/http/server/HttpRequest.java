package ru.gpncr.http.server;

import org.apache.logging.log4j.LogManager;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private static final org.apache.logging.log4j.Logger log = LogManager.getLogger(HttpRequest.class);

    private String rawRequest;
    private HttpMethod method;
    private String uri;
    private Map<String, String> parameters;
    private Exception exception;
    String body;
    private Map<String, String> headers;

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

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

    private void parse() {
        int startIndex = rawRequest.indexOf(' ');
        int endIndex = rawRequest.indexOf(' ', startIndex + 1);
        uri = rawRequest.substring(startIndex + 1, endIndex);
        method = HttpMethod.valueOf(rawRequest.substring(0, startIndex));
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

        if (method == HttpMethod.POST) {
            this.body = rawRequest.substring(rawRequest.indexOf("\r\n\r\n") + 4);
        }

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
        if (debug) {
            log.debug(rawRequest);
        }

        log.info("Method: " + method);
        log.info("Uri: " + uri);
        log.info("Parameter: " + parameters);
        log.info("Body: " + body);
        log.info("Header: " + headers);
    }

    public String getRoutingKey() {
        return method + " " + uri;
    }
}