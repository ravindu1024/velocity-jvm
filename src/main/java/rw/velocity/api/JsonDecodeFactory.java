package rw.velocity.api;

public interface JsonDecodeFactory {

    <T> T deserialize(String json, Class<T> clz);
}
