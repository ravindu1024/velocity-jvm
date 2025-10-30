package rw.velocity.api;

import java.net.http.HttpHeaders;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class VelocityLogger implements Logger {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

    private void printWithInfo(String line) {
        var thread = Thread.currentThread().getName();
        var timestamp = formatter.format(LocalDateTime.now());
        System.out.printf("[VELOCITY][%s][%s] %s%n", thread, timestamp, line);
    }

    @Override
    public void logRequest(String uri, String method, HttpHeaders headers, String body, List<String> formData) {

        // Url
        printWithInfo("--> " + method + " " + uri);

        // Headers
        headers.map().forEach((s, values) -> {
            if (values.isEmpty()) {
                printWithInfo(s + ":");
            } else {
                values.forEach(v -> printWithInfo(s + ": " + v));
            }
        });
        printWithInfo("");

        // Body
        if (body != null)
            printWithInfo(body);

        // Form Params
        if (formData != null) {
            formData.forEach(this::printWithInfo);
        }

        printWithInfo("--");
    }

    @Override
    public void logResponse(String uri, HttpHeaders headers, String body, int status, long timeMs) {
        // Headers
        headers.map().forEach((s, values) -> {
            if (values.isEmpty()) {
                printWithInfo(s + ":");
            } else {
                values.forEach(v -> printWithInfo(s + ": " + v));
            }
        });

        // Body
        printWithInfo(body.replace("\n", ""));

        // Url
        printWithInfo("<-- " + status + "(" + timeMs + "ms) " + uri + "\n");
    }
}
