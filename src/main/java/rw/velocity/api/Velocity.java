package rw.velocity.api;


public class Velocity {

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
}
