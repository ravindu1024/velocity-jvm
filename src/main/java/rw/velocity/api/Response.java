package rw.velocity.api;

import java.net.http.HttpHeaders;

/**
 * A wrapper class for {@link java.net.http.HttpResponse}
 */
public class Response {

    final boolean isSuccess;
    final int statusCode;
    final String body;
    final HttpHeaders headers;

    public Response(boolean isSuccess, int statusCode, String body, HttpHeaders headers) {
        this.isSuccess = isSuccess;
        this.statusCode = statusCode;
        this.body = body;
        this.headers = headers;
    }

    /**
     * Checks if the request was successful.
     *
     * @return true if the response code was 2xx
     */
    public boolean isSuccess() {
        return isSuccess;
    }

    /**
     * Get the http response code
     *
     * @return http response code
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Get the http response body as string
     *
     * @return http response body
     */
    public String getBody() {
        return body;
    }

    /**
     * Get the http response headers
     *
     * @return Headers
     */
    public HttpHeaders getHeaders() {
        return headers;
    }
}
