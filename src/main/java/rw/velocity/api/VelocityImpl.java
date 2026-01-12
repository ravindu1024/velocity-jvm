package rw.velocity.api;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.Executor;

class VelocityImpl implements Velocity {

    private final @Nullable String proxyUrl;
    private final @Nullable Integer proxyPort;
    private final @Nullable Duration connectTimeout;
    private final RedirectPolicy redirectPolicy;
    private final HttpVersion version;
    private final @Nullable JsonDecodeFactory decodeFactory;
    private final @Nullable Logger logger;
    private final HttpClient httpClient;
    private final RequestHandlerCallback callback = new RequestHandlerCallback() {
        @Override
        public Response executeRequest(RequestBuilderImpl requestBuilder) throws IOException, InterruptedException {
            return new RequestExecutor(requestBuilder, httpClient(), decodeFactory, logger).execute();
        }

        @Override
        public <T> T executeRequest(RequestBuilderImpl requestBuilder, Class<T> clz) throws HttpException, IOException, InterruptedException {
            return new RequestExecutor(requestBuilder, httpClient(), decodeFactory, logger).execute(clz);
        }
    };


    protected VelocityImpl(BuilderImpl builder){
        this.proxyUrl = builder.proxyUrl;
        this.proxyPort = builder.proxyPort;
        this.connectTimeout = builder.connectTimeout;
        this.redirectPolicy = builder.redirectPolicy;
        this.version = builder.version;
        this.logger = builder.logger;
        this.decodeFactory = builder.decodeFactory;

        //create httpclient
        httpClient = buildHttpClient();
    }

    private HttpClient buildHttpClient(){
        var httpClientBuilder = HttpClient.newBuilder();
        httpClientBuilder.version(version.v);
        httpClientBuilder.followRedirects(redirectPolicy.r);
        if(proxyUrl != null && proxyPort != null){
            httpClientBuilder.proxy(ProxySelector.of(new InetSocketAddress(proxyUrl, proxyPort)));
        }
        if(connectTimeout != null){
            httpClientBuilder.connectTimeout(connectTimeout);
        }

        return httpClientBuilder.build();
    }

    protected HttpClient httpClient(){
        return httpClient;
    }

    @Override
    public RequestBuilderImpl get(String url){
        return new RequestBuilderImpl(url, "GET", callback);
    }

    @Override
    public RequestBuilderImpl post(String url){
        return new RequestBuilderImpl(url, "POST", callback);
    }

    @Override
    public RequestBuilderImpl delete(String url){
        return new RequestBuilderImpl(url, "DELETE", callback);
    }

    @Override
    public RequestBuilderImpl put(String url){
        return new RequestBuilderImpl(url, "PUT", callback);
    }

    @Override
    public RequestBuilder call(String method, String url) throws HttpException {
        if(method.equalsIgnoreCase("GET") ||
                method.equalsIgnoreCase("PUT") ||
                method.equalsIgnoreCase("POST") ||
                method.equalsIgnoreCase("DELETE") ||
                method.equalsIgnoreCase("HEAD") ||
                method.equalsIgnoreCase("PATCH") ||
                method.equalsIgnoreCase("OPTIONS")
        ) {
            return new RequestBuilderImpl(url, method, callback);
        }else{
            throw new HttpException(0, "Invalid method");
        }
    }

    public static class BuilderImpl implements Velocity.Builder{
        private @Nullable String proxyUrl;
        private @Nullable Integer proxyPort;
        private @Nullable Duration connectTimeout;
        private RedirectPolicy redirectPolicy = RedirectPolicy.HTTPS_ONLY;
        private HttpVersion version = HttpVersion.V2_PREFERRED;
        private @Nullable Logger logger = null;
        private @Nullable JsonDecodeFactory decodeFactory = null;
        private @Nullable Executor executor = null;


        @Override
        public Velocity.Builder decodeFactory(JsonDecodeFactory decodeFactory){
            this.decodeFactory = decodeFactory;
            return this;
        }

        @Override
        public Velocity.Builder proxy(String url, int port){
            this.proxyUrl = url;
            this.proxyPort = port;
            return this;
        }

        @Override
        public Velocity.Builder connectTimeout(Duration timeout){
            this.connectTimeout = timeout;
            return this;
        }

        @Override
        public Velocity.Builder redirect(RedirectPolicy policy){
            this.redirectPolicy = policy;
            return this;
        }

        @Override
        public Velocity.Builder version(HttpVersion version){
            this.version = version;
            return this;
        }

        @Override
        public Velocity.Builder logger(Logger logger){
            this.logger = logger;
            return this;
        }

        @Override
        public Velocity build(){
            return new VelocityImpl(this);
        }
    }
}
