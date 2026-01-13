package rw.velocity.api;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.Executor;

/**
 * A lightweight wrapper for {@link java.net.http.HttpRequest} with convenience functions for setting query/form params,
 * headers and body.
 */
@SuppressWarnings("unused")
public interface Velocity {

    interface Builder {
        Velocity.Builder decodeFactory(JsonDecodeFactory decodeFactory);

        Velocity.Builder proxy(String url, int port);

        Velocity.Builder connectTimeout(Duration timeout);

        Velocity.Builder redirect(RedirectPolicy policy);

        Velocity.Builder version(HttpVersion version);

        Velocity.Builder executor(Executor executor);

        Velocity.Builder logger(Logger logger);

        Velocity.Builder userAgentOverride(String userAgent);

        Velocity build();
    }

    /**
     * Get method
     * @param url url
     * @return [RequestBuilder]
     */
    RequestBuilder get(String url);

    /**
     * Post method
     * @param url url
     * @return [RequestBuilder]
     */
    RequestBuilder post(String url);

    /**
     * Delete method
     * @param url url
     * @return [RequestBuilder]
     */
    RequestBuilder delete(String url);

    /**
     * Put method
     * @param url url
     * @return [RequestBuilder]
     */
    RequestBuilder put(String url);

    /**
     * Call http endpoint with given method type
     * @param url url
     * @param method Http method
     * @return [RequestBuilder]
     */
    RequestBuilder call(String method, String url) throws HttpException;


    static Builder newBuilder() {
        return new VelocityImpl.BuilderImpl();
    }

    enum RedirectPolicy {
        NEVER(HttpClient.Redirect.NEVER),
        HTTPS_ONLY(HttpClient.Redirect.NORMAL),
        ALWAYS(HttpClient.Redirect.ALWAYS);

        public final HttpClient.Redirect r;

        RedirectPolicy(HttpClient.Redirect r) {
            this.r = r;
        }
    }

    enum HttpVersion {
        V1(HttpClient.Version.HTTP_1_1),
        V2_PREFERRED(HttpClient.Version.HTTP_2);

        public final HttpClient.Version v;

        HttpVersion(HttpClient.Version v) {
            this.v = v;
        }
    }
}
