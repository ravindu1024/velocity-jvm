package rw.velocity.api;


import java.util.Base64;

public class Authentication {

    private final RequestBuilderImpl builder;

    Authentication(RequestBuilderImpl builder) {
        this.builder = builder;
    }

    public RequestBuilder bearer(String token){
        builder.authHeader = String.format("Bearer %s", token);
        return builder;
    }

    public RequestBuilder basic(String username, String password){
        var credentials = username + ":" + password;
        var encodedCredentials = Base64.getUrlEncoder().encodeToString(credentials.getBytes());

        builder.authHeader = "Basic " + encodedCredentials;
        return builder;
    }
}
