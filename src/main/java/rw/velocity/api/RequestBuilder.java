package rw.velocity.api;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Builds an http request with parameters
 */
@SuppressWarnings("unused")
public interface RequestBuilder {

    /**
     * Set authentication Basic or Bearer
     */
    Authentication authentication();

    /**
     * Set a single header string
     *
     * @param header header name
     * @param value  header value
     * @return RequestBuilder
     */
    RequestBuilder header(String header, String value);

    /**
     * Set multiple headers
     *
     * @param headers list of header values
     * @return RequestBuilder
     */
    RequestBuilder headers(Map<String, String> headers);

    /**
     * Set the 'Content-Type' header value from a list of common types
     *
     * @param contentType Http request content type
     * @return RequestBuilder
     */
    RequestBuilder contentType(ContentType contentType);

    /**
     * Set a custom 'Content-Type' header value
     *
     * @param contentType Http request content type
     * @return RequestBuilder
     */
    RequestBuilder contentType(String contentType);

    /**
     * Set a single query param
     *
     * @param name  param name
     * @param value param value
     * @return RequestBuilder
     */
    RequestBuilder queryParam(String name, String value);

    /**
     * Set a list of query params
     *
     * @param params list of params names and values
     * @return RequestBuilder
     */
    RequestBuilder queryParams(List<Pair<String, String>> params);

    /**
     * Set a form param value. Sets the content type to 'application/x-www-form-urlencoded' unless
     * otherwise specified.
     *
     * @param name  param name
     * @param value param value
     * @return RequestBuilder
     */
    RequestBuilder formParam(String name, String value);

    /**
     * Set multiple form params. Sets the content type to 'application/x-www-form-urlencoded' unless
     * otherwise specified.
     *
     * @param params list of param values
     * @return RequestBuilder
     */
    RequestBuilder formParams(List<Pair<String, String>> params);

    /**
     * Set the request body. Assumed to be encoded as required. Content type is set to 'application/json'
     * unless otherwise specified.
     *
     * @param body encoded body string
     * @return RequestBuilder
     */
    RequestBuilder body(String body);

    /**
     * Set an {@link InputStream} as the request body. Sets the content type to 'multipart/form-data'
     * unless otherwise specified.
     *
     * @param body InputStream
     * @return RequestBuilder
     */
    RequestBuilder body(String paramName, InputStream body);

    /**
     * Set a {@link File} as the request body. Sets the content type to 'multipart/form-data'
     * unless otherwise specified.
     *
     * @param body File
     * @return RequestBuilder
     */
    RequestBuilder body(String paramName, File body);

    /**
     * Set a request timeout in seconds
     * Set 0 for infinite
     *
     * @param seconds timeout seconds
     * @return RequestBuilder
     */
    RequestBuilder timeout(int seconds);

    /**
     * Make an http request synchronously. This function will block until the request is complete and response data
     * is available. Uses the Java 11 {@link java.net.http.HttpRequest} class under the hood.
     *
     * @return Response data
     * @throws IOException          IoException
     * @throws InterruptedException InterruptedException
     */
    Response request() throws IOException, InterruptedException;

    /**
     * Make an http request synchronously. This function will block until the request is complete and response data
     * is available. The response data will be deserialised based on the provided JsonDecoder.
     * Uses the Java 11 {@link java.net.http.HttpRequest} class under the hood.
     *
     * @param clz Class to map json to
     * @param <T> Mapped class type
     * @return <T> mapped object
     * @throws IOException          IoException
     * @throws InterruptedException InterruptedException
     * @throws HttpException        Http exception when a uri returns an error response
     */
    <T> T request(Class<T> clz) throws IOException, InterruptedException, HttpException;
}
