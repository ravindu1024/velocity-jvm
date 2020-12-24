package rw.velocity.api;


import java.util.Base64;

/**
 * Handles http authentication in the 2 most common methods: Basic and Bearer.
 */
public class Authentication {

    private final RequestBuilderImpl builder;

    Authentication(RequestBuilderImpl builder) {
        this.builder = builder;
    }

    /**
     * Sets a bearer token auth header to the request
     * @param token auth token
     * @return RequestBuilder
     */
    public RequestBuilder bearer(String token){
        builder.authHeader = String.format("Bearer %s", token);
        return builder;
    }

    /**
     * Sets an encoded basic auth header to the request
     * @param username username
     * @param password password
     * @return RequestBuilder
     */
    public RequestBuilder basic(String username, String password){
        var credentials = username + ":" + password;
        var encodedCredentials = Base64.getUrlEncoder().encodeToString(credentials.getBytes());

        builder.authHeader = "Basic " + encodedCredentials;
        return builder;
    }
}
