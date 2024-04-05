package org.machinemc.scriptive.serialization;

import java.util.*;

/**
 * Serializer for Java {@link Map}.
 */
public class MapComponentSerializer extends ComponentSerializer<Map<String, ?>> {

    @Override
    public Map<String, ?> serializeFromProperties(ComponentProperties properties) {
        Objects.requireNonNull(properties, "Component properties can not be null");
        Map<String, Object> map = new HashMap<>();
        Set<String> keys = properties.getKeys();
        for (String key : keys) map.put(key, unwrap(properties.get(key, ComponentProperty.class).orElseThrow()));
        return map;
    }

    @Override
    public ComponentProperties deserializeAsProperties(Map<String, ?> value) {
        Objects.requireNonNull(value, "Map can not be null");
        ComponentProperties properties = new ComponentProperties();
        value.forEach((k, v) -> properties.set(k, wrap(v)));
        return properties;
    }

    private Object unwrap(ComponentProperty<?> property) {
        return switch (property) {
            case ComponentProperty.Properties properties -> serializeFromProperties(properties.value());
            case ComponentProperty.Array array -> Arrays.stream(array.value()).map(this::serializeFromProperties).toList();
            default -> property.value();
        };
    }

    @SuppressWarnings("unchecked")
    private ComponentProperty<?> wrap(Object o) {
        return switch (o) {
            case Map<?, ?> map -> ComponentProperty.properties(deserializeAsProperties((Map<String, ?>) map));
            case List<?> list -> {
                ComponentProperties[] properties = new ComponentProperties[list.size()];
                for (int i = 0; i < properties.length; i++) {
                    ComponentProperty<?> next = wrap(list.get(i));
                    properties[i] = ComponentProperty.convertToProperties(next).value();
                }
                yield ComponentProperty.array(properties);
            }
            default -> ComponentProperty.of(o);
        };
    }

}
