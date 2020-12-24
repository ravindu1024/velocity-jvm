package rw.velocity.api;

/**
 * A lightweight wrapper for {@link java.net.http.HttpRequest} with convenience functions for setting query/form params,
 * headers and body.
 */
public class Velocity {

    static boolean ENABLE_LOGGING = false;

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

    public static void enableLogging(boolean enable){
        ENABLE_LOGGING = enable;
    }
}
