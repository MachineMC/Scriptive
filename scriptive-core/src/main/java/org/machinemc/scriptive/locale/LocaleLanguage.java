package org.machinemc.scriptive.locale;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * Represents a language document.
 */
public abstract class LocaleLanguage {

    private static final Pattern UNSUPPORTED_FORMAT_PATTERN = Pattern.compile("%(\\d+\\$)?[\\d.]*[df]");

    /**
     * Returns translation value for given key or the key itself in case
     * there is no value for given key.
     *
     * @param node translation key
     * @return translation
     */
    public String getOrDefault(String node) {
        return getOrDefault(node, node);
    }

    /**
     * Returns translation value for given key or the default value in case
     * there is no value for given key.
     *
     * @param node translation key
     * @param defaultValue default value to use
     * @return translation
     */
    public abstract String getOrDefault(String node, String defaultValue);

    /**
     * @param node translation key
     * @return whether this document contains entry for given key
     */
    public abstract boolean has(String node);

    /**
     * Creates local language from resource bundle.
     *
     * @param resourceBundle resource bundle
     * @return locale language
     */
    public static LocaleLanguage fromResourceBundle(ResourceBundle resourceBundle) {
        Map<String, String> map = new HashMap<>();
        Collections.list(resourceBundle.getKeys()).forEach(key -> {
            Object o = resourceBundle.getObject(key);
            if (!(o instanceof String s)) return;
            map.put(key, s);
        });
        return fromMap(map);
    }

    /**
     * Creates local language from a map.
     *
     * @param map map
     * @return locale language
     */
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
