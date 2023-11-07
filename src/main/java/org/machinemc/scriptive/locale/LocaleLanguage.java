package org.machinemc.scriptive.locale;

import com.google.gson.*;
import org.machinemc.scriptive.GsonInstance;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

public abstract class LocaleLanguage {

    private static final Pattern UNSUPPORTED_FORMAT_PATTERN = Pattern.compile("%(\\d+\\$)?[\\d.]*[df]");

    public String getOrDefault(String node) {
        return getOrDefault(node, node);
    }

    public abstract String getOrDefault(String node, String defaultValue);

    public abstract boolean has(String node);

    public static LocaleLanguage load(ClassLoader classLoader, String path) {
        Map<String, String> translations = new HashMap<>();
        parseTranslations(translations::put, classLoader, path);
        return new LocaleLanguage() {

            @Override
            public String getOrDefault(String node, String defaultValue) {
                return translations.getOrDefault(node, defaultValue);
            }

            @Override
            public boolean has(String node) {
                return translations.containsKey(node);
            }

        };
    }

    private static void parseTranslations(BiConsumer<String, String> consumer, ClassLoader classLoader, String path) {
        try (InputStream stream = classLoader.getResourceAsStream(path)) {
            loadFromJSON(stream, consumer);
        } catch (IOException | JsonParseException e) {
            throw new RuntimeException("Couldn't read strings from " + path, e);
        }
    }

    public static void loadFromJSON(InputStream stream, BiConsumer<String, String> consumer) {
        JsonObject json = GsonInstance.get().fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), JsonObject.class);
        json.entrySet().forEach(entry -> {
            String value = Optional.ofNullable(json.get(entry.getKey()))
                    .filter(JsonElement::isJsonPrimitive)
                    .map(JsonElement::getAsJsonPrimitive)
                    .map(JsonPrimitive::getAsString)
                    .orElseThrow(() -> new JsonSyntaxException("Expected " + entry.getKey() + " to be a string"));
            String translation = UNSUPPORTED_FORMAT_PATTERN.matcher(value).replaceAll("%$1s");
            consumer.accept(entry.getKey(), translation);
        });
    }

}
