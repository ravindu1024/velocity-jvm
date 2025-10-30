package rw.velocity.api;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;

public class TestableVelocity extends VelocityImpl {
    private final int status;
    private final String body;
    private final HttpClient client;

    protected HttpRequest request(){
        return ((FakeHttpClient)client).request;
    }

    protected TestableVelocity(VelocityImpl.BuilderImpl builder, int status, String body){
        super(builder);
        this.status = status;
        this.body = body;
        client = new FakeHttpClient(status, body);
    }

    @Override
    protected HttpClient httpClient() {
        return client;
    }
}
