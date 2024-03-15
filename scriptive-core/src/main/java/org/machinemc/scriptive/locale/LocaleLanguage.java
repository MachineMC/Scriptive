package org.machinemc.scriptive.locale;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public abstract class LocaleLanguage {

    private static final Pattern UNSUPPORTED_FORMAT_PATTERN = Pattern.compile("%(\\d+\\$)?[\\d.]*[df]");

    public String getOrDefault(String node) {
        return getOrDefault(node, node);
    }

    public abstract String getOrDefault(String node, String defaultValue);

    public abstract boolean has(String node);

    public static LocaleLanguage fromMap(Map<String, String> map) {
        Map<String, String> translations = new ConcurrentHashMap<>();
        map.forEach((key, translation) -> translations.put(key, UNSUPPORTED_FORMAT_PATTERN.matcher(translation).replaceAll("%$1s")));
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

}
