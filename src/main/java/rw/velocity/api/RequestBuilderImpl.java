package rw.velocity.api;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class RequestBuilderImpl implements RequestBuilder {
    protected final String requestUrl;
    protected HashMap<String, String> headers = new HashMap<>();
    protected ArrayList<Pair<String, String>> queryParams = new ArrayList<>();
    protected ArrayList<Pair<String, String>> postParams = new ArrayList<>();
    protected final String method;

    @Nullable
    protected String contentType = null;
    @Nullable
    protected String contentTypeAuto = null;
    @Nullable
    protected String authHeader;
    @Nullable
    protected String postBody;
    @Nullable
    protected InputStream postBodyStream;
    @Nullable
    protected File postBodyFile;
    @Nullable
    protected String postFileParamName;

    private RequestBuilderImpl(){
        this.requestUrl = "";
        this.method = "";
    }

    public RequestBuilderImpl(String url, String method) {
        this.requestUrl = url;
        this.method = method;
    }

    //region Authentication

    @Override
    public Authentication authentication(){
        return new Authentication(this);
    }

    //endregion

    //region Headers

    @Override
    public RequestBuilder header(String header, String value){
        this.headers.put(header, value);
        return this;
    }

    @Override
    public RequestBuilder headers(Map<String, String> headers){
        this.headers.putAll(headers);
        return this;
    }

    @Override
    public RequestBuilder contentType(ContentType contentType){
        this.contentType = contentType.type;
        return this;
    }

    @Override
    public RequestBuilder contentType(String contentType){
        this.contentType = contentType;
        return this;
    }

    //endregion

    //region Query Params

    @Override
    public RequestBuilder queryParam(String name, String value){
        this.queryParams.add(new Pair<>(name, value));
        return this;
    }

    @Override
    public RequestBuilder queryParams(List<Pair<String, String>> params){
        this.queryParams.addAll(params);
        return this;
    }

    //endregion

    //region Form/Body Params

    @Override
    public RequestBuilder formParam(String name, String value){
        this.contentTypeAuto = ContentType.URL_ENCODED.type;
        this.postParams.add(new Pair<>(name, value));
        return this;
    }

    @Override
    public RequestBuilder formParams(List<Pair<String, String>> params){
        this.contentTypeAuto = ContentType.URL_ENCODED.type;
        this.postParams.addAll(params);
        return this;
    }

    @Override
    public RequestBuilder body(String body){
        this.postBody = body;
        this.contentTypeAuto = ContentType.APPLICATION_JSON.type;
        return this;
    }

    @Override
    public RequestBuilder body(String paramName, InputStream body){
        this.postBodyStream = body;
        this.postBodyFile = null;
        this.postFileParamName = paramName;
        this.contentTypeAuto = ContentType.MULTIPART_FORM.type;
        return this;
    }

    @Override
    public RequestBuilder body(String paramName, File body){
        this.postBodyFile = body;
        this.postBodyStream = null;
        this.postFileParamName = paramName;
        this.contentTypeAuto = ContentType.MULTIPART_FORM.type;
        return this;
    }

    //endregion

    @Override
    public Response request() throws IOException, InterruptedException {
        return new Executor(this).execute();
    }
}
