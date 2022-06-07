package rw.velocity.api;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;


/**
 * Creates the http request objects and executes the actual request.
 * Uses {@link HttpRequest}
 */
class Executor {

    private final RequestBuilderImpl builder;
    private final Response response = new Response();
    private @Nullable String contentType;

    public Executor(RequestBuilderImpl builder) {
        this.builder = builder;
    }

    public Response execute() throws IOException, InterruptedException {

        // Setup request builder
        String fullUrl = builder.requestUrl + createQueryString();
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(URI.create(fullUrl));

        if(builder.timeout > 0){
            requestBuilder.timeout(Duration.of(builder.timeout, ChronoUnit.SECONDS));
        } else if(Velocity.TIMEOUT_SECONDS > 0){
            requestBuilder.timeout(Duration.of(Velocity.TIMEOUT_SECONDS, ChronoUnit.SECONDS));
        }

        // Setup headers
        setupHeaders(requestBuilder);

        // Setup body
        setupBody(requestBuilder);

        // Create request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = requestBuilder.build();

        // Debug prints
        debugPrintRequest(request);

        // Make request
        var r = client.send(request, HttpResponse.BodyHandlers.ofString());

        //Debug prints
        debugPrintResponse(r);

        // Setup response
        response.body = r.body();
        response.isSuccess = r.statusCode() / 100 == 2;
        response.statusCode = r.statusCode();
        response.headers = r.headers();

        return response;
    }

    private void setupHeaders(HttpRequest.Builder requestBuilder) {
        builder.headers.forEach(requestBuilder::setHeader);

        if (builder.contentType != null) {
            contentType = builder.contentType;
        }else{
            contentType = builder.contentTypeAuto;
        }

        if(contentType != null){
            String boundary =
                    (contentType.equalsIgnoreCase(ContentType.MULTIPART_FORM.type))
                            ? "; boundary=" + MultiPartPublisher.MULTIPART_BOUNDARY
                            : "";

            requestBuilder.setHeader("Content-Type", contentType + boundary);
        }

        if (builder.authHeader != null) {
            requestBuilder.setHeader("Authorization", builder.authHeader);
        }

        requestBuilder.setHeader("User-Agent", "github.com/ravindu1024/velocity-jvm");
    }

    private void debugPrintRequest(HttpRequest request){
        if(Velocity.ENABLE_LOGGING){
            // Url
            System.out.println("--> " + request.uri().toString());
            // Headers
            request.headers().map().forEach((s, values) -> {
                System.out.print("> " + s + ": ");
                if(!values.isEmpty()){
                    System.out.println(values.get(0));
                }
            });
            System.out.println();

            // Body
            if(builder.postBody != null)
                System.out.println(builder.postBody);

            // Form Params
            if(builder.multiPartData != null){
                builder.multiPartData.forEach(b -> {
                    System.out.println(new String(b));
                });
                System.out.println();
            }
        }
    }

    private <T> void debugPrintResponse(HttpResponse<T> response){
        if(Velocity.ENABLE_LOGGING){
            // Headers
            response.headers().map().forEach((s, values) -> {
                System.out.print("< " + s + ": ");
                if(!values.isEmpty()){
                    System.out.println(values.get(0));
                }
            });

            // Body
            System.out.println(response.body());
            // Url
            System.out.println("<-- " + response.statusCode()  + " " + response.uri().toString());
        }
    }

    private void setupBody(HttpRequest.Builder requestBuilder) throws IOException {
        // Setup body
        if (builder.method.equalsIgnoreCase("GET")) {
            requestBuilder.GET();
        } else if (builder.method.equalsIgnoreCase("DELETE")) {
            requestBuilder.DELETE();
        } else {

            if(contentType != null && contentType.startsWith("multipart")){
                HashMap<Object, Object> params = new HashMap<>();
                builder.postParams.forEach(p -> {
                    params.put(p.first, p.second);
                });

                if (builder.postBodyFile != null) {
                    params.put(builder.postFileParamName, Paths.get(builder.postBodyFile.getAbsolutePath()));
                    requestBuilder.method(
                            builder.method,
                            MultiPartPublisher.ofMimeMultipartData(params, null)
                    );
                } else if (builder.postBodyStream != null) {
                    params.put(builder.postFileParamName, builder.postBodyStream);
                    requestBuilder.method(
                            builder.method,
                            MultiPartPublisher.ofMimeMultipartData(params, null)
                    );
                }else{
                    requestBuilder.method(
                            builder.method,
                            MultiPartPublisher.ofMimeMultipartData(params, builder)
                    );
                }
            }else {
                requestBuilder.method(
                        builder.method,
                        HttpRequest.BodyPublishers.ofString(createBodyString())
                );
            }
        }
    }

    private String createQueryString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < builder.queryParams.size(); i++) {
            Pair<String, String> p = builder.queryParams.get(i);

            sb.append((i == 0) ? "?" : "&")
                    .append(urlEncode(p.first))
                    .append("=")
                    .append(urlEncode(p.second));
        }
        return sb.toString();
    }

    private String createBodyString() {
        if (builder.postBody == null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < builder.postParams.size(); i++) {
                Pair<String, String> p = builder.postParams.get(i);

                sb.append((i == 0) ? "" : "&")
                        .append(urlEncode(p.first))
                        .append("=")
                        .append(urlEncode(p.second));
            }

            return sb.toString();
        } else {
            return builder.postBody;
        }
    }

    private String urlEncode(String str) {
        return URLEncoder.encode(str, StandardCharsets.UTF_8);
    }
}
