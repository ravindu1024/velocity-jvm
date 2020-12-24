package rw.velocity.api;

public enum ContentType {

    ANY("*/*"),
    APPLICATION_JSON("application/json"),
    MULTIPART_FORM("multipart/form-data"),
    URL_ENCODED("application/x-www-form-urlencoded"),
    TEXT_PLAIN("text/plain");

    final String type;

    ContentType(String type) {
        this.type = type;
    }
}
