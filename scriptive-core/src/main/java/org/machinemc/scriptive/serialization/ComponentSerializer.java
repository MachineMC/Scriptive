package org.machinemc.scriptive.serialization;

import org.jetbrains.annotations.Nullable;
import org.machinemc.scriptive.components.Component;
import org.machinemc.scriptive.components.KeybindComponent;
import org.machinemc.scriptive.components.TextComponent;
import org.machinemc.scriptive.components.TranslationComponent;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A {@link Component} serializer and deserializer.
 */
public class ComponentSerializer {

    private final Set<Class<? extends Component>> registered = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private final Map<String, Class<? extends Component>> componentNames = new ConcurrentHashMap<>();
    private final Map<String, Class<? extends Component>> uniqueKeys = new ConcurrentHashMap<>();

    private final Map<Class<? extends Component>, Function<ComponentProperties, ? extends Component>> componentCreators = new ConcurrentHashMap<>();

    /**
     * Creates new component serializer and automatically registers the 3
     * default components for vanilla Minecraft client.
     */
    public ComponentSerializer() {
        register(KeybindComponent.class, () -> KeybindComponent.of(""));
        register(TextComponent.class, TextComponent::empty);
        register(TranslationComponent.class, () -> TranslationComponent.of(""));
    }

    /**
     * Registers new component type for this serializer.
     *
     * @param type type of the component to register
     * @param emptySupplier supplier for the component of given type
     * @param <C> component type
     */
    public <C extends Component> void register(Class<C> type, Supplier<C> emptySupplier) {
        C empty = emptySupplier.get();

        if (registered.contains(empty.getType())) return;
        registered.add(empty.getType());

        if (empty.getName() != null) componentNames.putIfAbsent(empty.getName(), type);
        empty.getUniqueKeys().forEach(key -> uniqueKeys.putIfAbsent(key, type));

        componentCreators.put(type, properties -> {
            C component = emptySupplier.get();
            component.loadProperties(properties, this);
            return component;
        });
    }

    /**
     * Serializes the given component.
     *
     * @param component component
     * @return output
     */
    public <T> T serialize(Component component, PropertiesSerializer<T> propertiesSerializer) {
        return propertiesSerializer.serialize(serialize(component));
    }

    /**
     * Serializes the given component.
     *
     * @param component component
     * @return output
     */
    public ComponentProperties serialize(Component component) {
        if (!registered.contains(component.getType()))
            throw new UnsupportedOperationException("Serializer does not support components of type " + component.getType().getName());
        return component.getProperties();
    }

    /**
     * Deserializes the given component.
     *
     * @param value input
     * @return component
     */
    public <T> Component deserialize(T value, PropertiesSerializer<T> propertiesSerializer) {
        return deserialize(propertiesSerializer.deserialize(value));
    }

    /**
     * Deserializes the given component.
     *
     * @param properties input
     * @return component
     */
    public Component deserialize(ComponentProperties properties) {
        Class<? extends Component> type = getComponentTypeFromProperties(properties);
        if (type == null) throw new IllegalArgumentException("Unknown serialized component type");
        return newComponent(type, properties);
    }

    private @Nullable Class<? extends Component> getComponentTypeFromProperties(ComponentProperties properties) {
        if (properties.contains("type")) {
            String type = properties.getValue("type", String.class).orElseThrow();
            if (!componentNames.containsKey(type)) throw new UnsupportedOperationException("Serializer does not support components of type " + type);
            return componentNames.get(type);
        }
        for (String key : properties.getKeys()) {
            Class<? extends Component> type = uniqueKeys.get(key);
            if (type != null) return type;
        }
        return null;
    }

    private Component newComponent(Class<? extends Component> type, ComponentProperties properties) {
        if (!componentCreators.containsKey(type))
            throw new UnsupportedOperationException("Serializer does not support components of type " + type.getName());
        return componentCreators.get(type).apply(properties);
    }

}
