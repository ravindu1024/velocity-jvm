package rw.velocity.api;

import java.net.http.HttpHeaders;

/**
 * A wrapper class for {@link java.net.http.HttpResponse}
 */
public class Response {

    boolean isSuccess = false;
    int statusCode = 0;
    String body = "";
    HttpHeaders headers;

    /**
     * Checks if the request was successful.
     * @return true if the response code was 2xx
     */
    public boolean isSuccess() {
        return isSuccess;
    }

    /**
     * Get the http response code
     * @return http response code
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Get the http response body as string
     * @return http response body
     */
    public String getBody() {
        return body;
    }

    /**
     * Get the http response headers
     * @return Headers
     */
    public HttpHeaders getHeaders() {
        return headers;
    }
}
