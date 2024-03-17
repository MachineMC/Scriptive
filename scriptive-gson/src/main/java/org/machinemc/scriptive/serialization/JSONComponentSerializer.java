package org.machinemc.scriptive.serialization;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.machinemc.scriptive.components.ClientComponent;

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
    public ClientComponent deserialize(String input) {
        return deserializeJSON(JsonParser.parseString(input));
    }

    private ClientComponent deserializeJSON(JsonElement json) {
        Object o = null;

        if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString())
            o = json.getAsString();

        else if (json.isJsonArray())
            o = json.getAsJsonArray().asList().stream().map(this::deserializeJSON).toList();

        else if (json.isJsonObject())
            o = gson.fromJson(json, new TypeToken<Map<String, Object>>() {}.getType());

        return ObjectComponentSerializer.get().deserialize(o);
    }

    @Override
    public String serialize(ClientComponent component) {
        return gson.toJson(component.asMap(), new TypeToken<Map<String, Object>>() {}.getType());
    }

}
