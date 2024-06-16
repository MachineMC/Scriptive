package org.machinemc.scriptive.serialization;

import java.util.*;

/**
 * Serializer for Java {@link Map}.
 */
public class MapPropertiesSerializer implements PropertiesSerializer<Map<String, ?>> {

    private static final MapPropertiesSerializer INSTANCE = new MapPropertiesSerializer();

    private MapPropertiesSerializer() {}

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, ?> serialize(ComponentProperties properties) {
        Objects.requireNonNull(properties, "Component properties can not be null");
        return (Map<String, ?>) unwrap(ComponentProperty.properties(properties));
    }

    @Override
    public ComponentProperties deserialize(Map<String, ?> value) {
        Objects.requireNonNull(value, "Map can not be null");
        return ComponentProperty.convertToProperties(wrap(value)).value();
    }

    private Object unwrap(ComponentProperty<?> property) {
        return switch (property) {
            case ComponentProperty.Properties properties -> {
                Map<String, Object> map = new HashMap<>();
                properties.value().forEach((k, p) -> map.put(k, unwrap(p)));
                yield map;
            }
            case ComponentProperty.Array array -> {
                List<Object> list = new ArrayList<>();
                Arrays.stream(array.value())
                        .map(ComponentProperty::properties)
                        .map(this::unwrap)
                        .forEach(list::add);
                yield list;
            }
            default -> property.value();
        };
    }

    private ComponentProperty<?> wrap(Object o) {
        return switch (o) {
            case Map<?, ?> map -> {
                ComponentProperties properties = new ComponentProperties();
                map.forEach((k, e) -> properties.set((String) k, wrap(e)));
                yield ComponentProperty.properties(properties);
            }
            case List<?> list -> {
                ComponentProperties[] array = list.stream()
                        .map(this::wrap)
                        .map(ComponentProperty::convertToProperties)
                        .map(ComponentProperty::value)
                        .toArray(ComponentProperties[]::new);
                yield ComponentProperty.array(array);
            }
            default -> ComponentProperty.of(o);
        };
    }

    public static MapPropertiesSerializer get() {
        return INSTANCE;
    }

}
