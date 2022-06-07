package rw.velocity.api;

/**
 * A lightweight wrapper for {@link java.net.http.HttpRequest} with convenience functions for setting query/form params,
 * headers and body.
 */
public class Velocity {

    static boolean ENABLE_LOGGING = false;
    static int TIMEOUT_SECONDS = 0;

    public static RequestBuilder get(String url){
        return new RequestBuilderImpl(url, "GET");
    }

    public static RequestBuilder post(String url){
        return new RequestBuilderImpl(url, "POST");
    }

    public static RequestBuilder delete(String url){
        return new RequestBuilderImpl(url, "DELETE");
    }

    public static RequestBuilder put(String url){
        return new RequestBuilderImpl(url, "PUT");
    }


    /**
     * Enable or disable request logging
     * @param enable set true to enable logging
     */
    public static void enableLogging(boolean enable){
        ENABLE_LOGGING = enable;
    }

    /**
     * Set a connection timeout
     * @param timeout timeout in seconds
     */
    public static void setTimeout(int timeout) { TIMEOUT_SECONDS = timeout; }
}
