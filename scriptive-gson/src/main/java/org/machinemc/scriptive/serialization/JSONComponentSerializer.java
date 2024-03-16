package org.machinemc.scriptive.serialization;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.machinemc.scriptive.components.VanillaComponent;

import java.util.Map;

public class JSONComponentSerializer implements ComponentSerializer<String> {

    private static final JSONComponentSerializer INSTANCE = new JSONComponentSerializer();

    private final Gson gson = new Gson();

    public static JSONComponentSerializer get() {
        return INSTANCE;
    }

    private JSONComponentSerializer() {
    }

    @Override
    public VanillaComponent deserialize(String input) {
        return deserializeJSON(JsonParser.parseString(input));
    }

    private VanillaComponent deserializeJSON(JsonElement json) {
        Map<String, Object> map = gson.fromJson(json, new TypeToken<Map<String, Object>>() {}.getType());
        return MapComponentSerializer.get().deserialize(map);
    }

    @Override
    public String serialize(VanillaComponent component) {
        return gson.toJson(component.asMap(), new TypeToken<Map<String, Object>>() {}.getType());
    }

}
