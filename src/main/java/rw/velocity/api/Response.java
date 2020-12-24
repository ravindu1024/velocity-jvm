package rw.velocity.api;

import java.net.http.HttpHeaders;

public class Response {

    boolean isSuccess = false;
    int statusCode = 0;
    String body = "";
    HttpHeaders headers;


    public boolean isSuccess() {
        return isSuccess;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getBody() {
        return body;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }
}
