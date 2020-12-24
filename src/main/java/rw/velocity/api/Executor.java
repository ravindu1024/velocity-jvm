package rw.velocity.api;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.HashMap;

class Executor {

    private final RequestBuilderImpl builder;
    private final Response response = new Response();

    public Executor(RequestBuilderImpl builder) {
        this.builder = builder;
    }

    public Response execute() throws IOException, InterruptedException {
        createConnection();

        return response;
    }

    private void createConnection() throws IOException, InterruptedException {
        String fullUrl = builder.requestUrl + createQueryString();
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(URI.create(fullUrl));

        // Setup headers
        setupHeaders(requestBuilder);

        // Setup body
        setupBody(requestBuilder);

        // Make request
        HttpClient client = HttpClient.newHttpClient();
        var r = client.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());

        response.body = r.body();
        response.isSuccess = r.statusCode() / 100 == 2;
        response.statusCode = r.statusCode();
        response.headers = r.headers();
    }

    private void setupHeaders(HttpRequest.Builder requestBuilder) {
        builder.headers.forEach(requestBuilder::setHeader);

        String contentTypeToSet = null;

        if (builder.contentType != null) {
            contentTypeToSet = builder.contentType;
        }else{
            contentTypeToSet = builder.contentTypeAuto;
        }

        if(contentTypeToSet != null){
            String boundary =
                    (contentTypeToSet.equalsIgnoreCase(ContentType.MULTIPART_FORM.type)) ?
                            "; boundary=" + MultiPartPublisher.MULTIPART_BOUNDARY :
                            "";

            requestBuilder.setHeader("Content-Type", contentTypeToSet + boundary);
        }

        if (builder.authHeader != null) {
            requestBuilder.setHeader("Authorization", builder.authHeader);
        }

        requestBuilder.setHeader("User-Agent", "github.com/ravindu1024/velocity-jvm");
    }

    private void setupBody(HttpRequest.Builder requestBuilder) throws IOException {
        // Setup body
        if (builder.method.equalsIgnoreCase("GET")) {
            requestBuilder.GET();
        } else if (builder.method.equalsIgnoreCase("DELETE")) {
            requestBuilder.DELETE();
        } else {

            HashMap<Object, Object> params = new HashMap<>();
            builder.postParams.forEach(p -> {
                params.put(p.first, p.second);
            });

            if (builder.postBodyFile != null) {
                params.put(builder.postFileParamName, Paths.get(builder.postBodyFile.getAbsolutePath()));
                requestBuilder.method(
                        builder.method,
                        MultiPartPublisher.ofMimeMultipartData(params)
                );
            } else if (builder.postBodyStream != null) {
                params.put(builder.postFileParamName, builder.postBodyStream);
                requestBuilder.method(
                        builder.method,
                        MultiPartPublisher.ofMimeMultipartData(params)
                );
            } else {
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
