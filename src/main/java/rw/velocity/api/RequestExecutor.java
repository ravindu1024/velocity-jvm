package rw.velocity.api;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;


/**
 * Makes an http call using the created HttpClient and provided RequestBuilder data
 *
 */
class RequestExecutor {

    private final String requestUrl;
    private final HashMap<String, String> headers;
    private final ArrayList<Pair<String, String>> queryParams;
    private final ArrayList<Pair<String, String>> postParams;
    private final String method;
    private final String authHeader;
    private final String postBody;
    private final InputStream postBodyStream;
    private final File postBodyFile;
    private final String postFileParamName;
    private final int connectionTimeout;
    private final String userAgent;
    private final ArrayList<byte[]> multiPartData = new ArrayList<>();
    private final @Nullable String contentType;

    private final @Nullable JsonDecodeFactory decodeFactory;
    private final @Nullable Logger logger;
    private final HttpClient httpClient;


    RequestExecutor(RequestBuilderImpl builder, HttpClient httpClient, @Nullable JsonDecodeFactory decodeFactory, @Nullable Logger logger, String userAgent) {
        this.requestUrl = builder.requestUrl;
        this.headers = builder.headers;
        this.queryParams = builder.queryParams;
        this.postParams = builder.postParams;
        this.method = builder.method;
        this.contentType = builder.contentType != null ? builder.contentType : builder.contentTypeAuto;
        this.authHeader = builder.authHeader;
        this.postBody = builder.postBody;
        this.postBodyStream = builder.postBodyStream;
        this.postBodyFile = builder.postBodyFile;
        this.postFileParamName = builder.postFileParamName;
        this.connectionTimeout = builder.timeout;

        this.decodeFactory = decodeFactory;
        this.logger = logger;
        this.httpClient = httpClient;
        this.userAgent = userAgent;
    }

    <T> T execute(Class<T> clz) throws IOException, InterruptedException, HttpException {
        var response = execute();
        if(response.isSuccess()){
            if(decodeFactory == null){
                throw new IllegalStateException("No decoder factory");
            }
            return decodeFactory.deserialize(response.getBody(), clz);
        }else{
            throw new HttpException(response.getStatusCode(), response);
        }
    }

    Response execute() throws IOException, InterruptedException {

        // Setup request builder
        String fullUrl = requestUrl + createQueryString();
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(URI.create(fullUrl));

        if(connectionTimeout > 0){
            requestBuilder.timeout(Duration.of(connectionTimeout, ChronoUnit.SECONDS));
        }

        // Setup headers
        setupHeaders(requestBuilder);

        // Setup body
        setupBody(requestBuilder);


        HttpRequest request = requestBuilder.build();

        // Debug prints
        debugPrintRequest(request);

        // Make request
        var t = System.currentTimeMillis();
        var r = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        //Debug prints
        debugPrintResponse(r, System.currentTimeMillis() - t);

        // Setup response

        return new Response(
                /*isSuccess*/r.statusCode() / 100 == 2,
                /*statusCode*/r.statusCode(),
                /*body*/r.body(),
                /*headers*/r.headers()
        );
    }

    private void setupHeaders(HttpRequest.Builder requestBuilder) {
        headers.forEach(requestBuilder::setHeader);

        if(contentType != null){
            String boundary =
                    (contentType.equalsIgnoreCase(ContentType.MULTIPART_FORM.type))
                            ? "; boundary=" + Constants.MULTIPART_BOUNDARY
                            : "";

            requestBuilder.setHeader("Content-Type", contentType + boundary);
        }

        if (authHeader != null) {
            requestBuilder.setHeader("Authorization", authHeader);
        }

        String ua = userAgent != null ? userAgent : "github.com/ravindu1024/velocity-jvm";
        requestBuilder.setHeader("User-Agent", ua);
    }

    private void debugPrintRequest(HttpRequest request){

        if(logger != null){

            logger.logRequest(
                    /*url*/ request.uri().toString(),
                    /*method*/ request.method(),
                    /*headers*/ request.headers(),
                    /*body*/ postBody,
                    /*formData*/ multiPartData.stream().map(String::new).collect(Collectors.toList())
            );
        }

    }

    private <T> void debugPrintResponse(HttpResponse<T> response, long time){
        if(logger != null){
            logger.logResponse(
                    /*uri*/ response.uri().toString(),
                    /*headers*/ response.headers(),
                    /*body*/ response.body().toString(),
                    /*status*/ response.statusCode(),
                    /*timeMs*/ time
            );
        }
    }

    private void setupBody(HttpRequest.Builder requestBuilder) throws IOException {
        // Setup body
        if (method.equalsIgnoreCase("GET")) {
            requestBuilder.GET();
        } else if (method.equalsIgnoreCase("DELETE")) {
            requestBuilder.DELETE();
        } else {

            if(contentType != null && contentType.startsWith("multipart")){
                HashMap<Object, Object> params = new HashMap<>();
                postParams.forEach(p -> {
                    params.put(p.first, p.second);
                });

                if (postBodyFile != null) {
                    params.put(postFileParamName, Paths.get(postBodyFile.getAbsolutePath()));
                    requestBuilder.method(
                            method,
                            MultiPartPublisher.ofMimeMultipartData(params, null)
                    );
                } else if (postBodyStream != null) {
                    params.put(postFileParamName, postBodyStream);
                    requestBuilder.method(
                            method,
                            MultiPartPublisher.ofMimeMultipartData(params, null)
                    );
                }else{
                    requestBuilder.method(
                            method,
                            MultiPartPublisher.ofMimeMultipartData(params, multiPartData)
                    );
                }
            }else {
                requestBuilder.method(
                        method,
                        HttpRequest.BodyPublishers.ofString(createBodyString())
                );
            }
        }
    }

    private String createQueryString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < queryParams.size(); i++) {
            Pair<String, String> p = queryParams.get(i);

            sb.append((i == 0) ? "?" : "&")
                    .append(urlEncode(p.first))
                    .append("=")
                    .append(urlEncode(p.second));
        }
        return sb.toString();
    }

    private String createBodyString() {
        if (postBody == null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < postParams.size(); i++) {
                Pair<String, String> p = postParams.get(i);

                sb.append((i == 0) ? "" : "&")
                        .append(urlEncode(p.first))
                        .append("=")
                        .append(urlEncode(p.second));
            }

            return sb.toString();
        } else {
            return postBody;
        }
    }

    private String urlEncode(String str) {
        return URLEncoder.encode(str, StandardCharsets.UTF_8);
    }
}
