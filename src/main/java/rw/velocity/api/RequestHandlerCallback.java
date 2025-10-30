package rw.velocity.api;

import java.io.IOException;

abstract class RequestHandlerCallback {
    abstract Response executeRequest(RequestBuilderImpl requestBuilder) throws IOException, InterruptedException;

    abstract <T> T executeRequest(RequestBuilderImpl requestBuilder, Class<T> clz) throws HttpException, IOException, InterruptedException;
}
