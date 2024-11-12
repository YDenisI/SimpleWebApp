package ru.gpncr.http.server.processors;


import ru.gpncr.http.server.ExceptionHandler;
import ru.gpncr.http.server.HttpRequest;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;

public interface RequestProcessor {
    void execute(HttpRequest request, OutputStream output) throws IOException, ExceptionHandler;
}
