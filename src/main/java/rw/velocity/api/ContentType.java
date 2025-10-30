package rw.velocity.api;

/**
 * Content types for Http requests. This will be set in the Content-Type header
 */
@SuppressWarnings("unused")
public enum ContentType {

    ANY("*/*"),
    APPLICATION_JSON("application/json"),
    MULTIPART_FORM("multipart/form-data"),
    URL_ENCODED("application/x-www-form-urlencoded"),
    TEXT_PLAIN("text/plain");

    public final String type;

    ContentType(String type) {
        this.type = type;
    }
}
