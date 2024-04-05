package org.machinemc.scriptive.serialization;

import com.google.gson.*;

import java.util.Arrays;
import java.util.Objects;

/**
 * Serializer for JSON string representation.
 */
public class JSONComponentSerializer extends ComponentSerializer<String> {

    private final Gson gson;

    public JSONComponentSerializer() {
        this(new Gson());
    }

    public JSONComponentSerializer(Gson gson) {
        this.gson = Objects.requireNonNull(gson, "GSON instance can not be null");
    }

    @Override
    public String serializeFromProperties(ComponentProperties properties) {
        JsonElement element = unwrap(ComponentProperty.properties(properties));
        return gson.toJson(element);
    }

    @Override
    public ComponentProperties deserializeAsProperties(String value) {
        JsonElement element = JsonParser.parseString(value);
        return ComponentProperty.convertToProperties(wrap(element)).value();
    }

    private JsonElement unwrap(ComponentProperty<?> property) {
        return switch (property) {
            case ComponentProperty.String string -> new JsonPrimitive(string.value());
            case ComponentProperty.Boolean bool -> new JsonPrimitive(bool.value());
            case ComponentProperty.Integer integer -> new JsonPrimitive(integer.value());
            case ComponentProperty.Properties properties -> {
                JsonObject json = new JsonObject();
                properties.value().forEach((k, p) -> json.add(k, unwrap(p)));
                yield json;
            }
            case ComponentProperty.Array array -> {
                JsonArray json = new JsonArray();
                Arrays.stream(array.value())
                        .map(ComponentProperty::properties)
                        .map(this::unwrap)
                        .forEach(json::add);
                yield json;
            }
        };
    }

    private ComponentProperty<?> wrap(JsonElement element) {
        return switch (element) {
            case JsonArray json -> {
                ComponentProperties[] array = json.getAsJsonArray().asList().stream()
                        .map(this::wrap)
                        .map(ComponentProperty::convertToProperties)
                        .map(ComponentProperty::value)
                        .toArray(ComponentProperties[]::new);
                yield ComponentProperty.array(array);
            }
            case JsonObject json -> {
                ComponentProperties properties = new ComponentProperties();
                json.asMap().forEach((k, e) -> properties.set(k, wrap(e)));
                yield ComponentProperty.properties(properties);
            }
            case JsonPrimitive primitive when primitive.isString() -> ComponentProperty.string(primitive.getAsString());
            case JsonPrimitive primitive when primitive.isBoolean() -> ComponentProperty.bool(primitive.getAsBoolean());
            case JsonPrimitive primitive when primitive.isNumber() -> ComponentProperty.integer(primitive.getAsInt());
            default -> throw new IllegalStateException("Unexpected value: " + element);
        };
    }

}
