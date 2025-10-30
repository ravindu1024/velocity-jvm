package rw.velocity.api;

import com.google.gson.Gson;

public class GsonDecoderFactory implements JsonDecodeFactory{

    private final Gson gson = new Gson();

    @Override
    public <T> T deserialize(String json, Class<T> clz) {
        return gson.fromJson(json, clz);
    }
}
