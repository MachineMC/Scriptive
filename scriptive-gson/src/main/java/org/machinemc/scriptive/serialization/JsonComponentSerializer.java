package org.machinemc.scriptive.serialization;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.machinemc.scriptive.components.VanillaComponent;

import java.util.Map;

public class JsonComponentSerializer implements ComponentSerializer<String> {

    private static final JsonComponentSerializer INSTANCE = new JsonComponentSerializer();

    private final Gson gson = new Gson();

    public static JsonComponentSerializer get() {
        return INSTANCE;
    }

    private JsonComponentSerializer() {
    }

    @Override
    public VanillaComponent deserialize(String input) {
        return deserializeJson(JsonParser.parseString(input));
    }

    private VanillaComponent deserializeJson(JsonElement json) {
        Map<String, Object> map = gson.fromJson(json, new TypeToken<Map<String, Object>>() {}.getType());
        return MapComponentSerializer.get().deserialize(map);
    }

    @Override
    public String serialize(VanillaComponent component) {
        return gson.toJson(component.asMap(), new TypeToken<Map<String, Object>>() {}.getType());
    }

}
