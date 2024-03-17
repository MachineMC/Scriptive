package org.machinemc.scriptive.serialization;

import org.machinemc.scriptive.components.ClientComponent;
import org.machinemc.scriptive.components.TextComponent;

import java.util.List;
import java.util.Map;

public class ObjectComponentSerializer implements ComponentSerializer<Object> {

    private static final ObjectComponentSerializer INSTANCE = new ObjectComponentSerializer();

    public static ObjectComponentSerializer get() {
        return INSTANCE;
    }

    private ObjectComponentSerializer() {
    }

    @Override
    @SuppressWarnings("unchecked")
    public ClientComponent deserialize(Object input) {
        return switch (input) {
            case null -> throw new NullPointerException();
            case String s -> TextComponent.of(s);
            case Map<?, ?> map -> MapComponentSerializer.get().deserialize((Map<String, Object>) map);
            case List<?> list -> {
                if (list.isEmpty()) yield TextComponent.empty();
                ClientComponent first = deserialize(list.getFirst());
                for (int i = 1; i < list.size(); i++) first.append(deserialize(list.get(i)));
                yield first;
            }
            default -> throw new IllegalArgumentException("Unexpected type " + input.getClass().getName());
        };
    }

    @Override
    public Object serialize(ClientComponent component) {
        return MapComponentSerializer.get().serialize(component);
    }

}
