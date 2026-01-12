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

        Velocity.Builder logger(Logger logger);

        Velocity build();
    }

    RequestBuilder get(String url);

    RequestBuilder post(String url);

    RequestBuilder delete(String url);

    RequestBuilder put(String url);

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
