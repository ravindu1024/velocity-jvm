package rw.velocity.api;

import java.net.http.HttpHeaders;
import java.util.List;

public interface Logger {
    void logRequest(String uri, String method, HttpHeaders headers, String body, List<String> formData);
    void logResponse(String uri, HttpHeaders headers, String body, int status, long timeMs);
}

