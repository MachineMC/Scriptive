package org.machinemc.scriptive.serialization;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.function.BiConsumer;

/**
 * Represents a custom map created out of objects that can be
 * serialized using component serializers.
 */
public sealed class ComponentProperties {

    private final Map<String, ComponentProperty<?>> propertyMap = new TreeMap<>();

    /**
     * Creates deep copy of another properties.
     *
     * @param other properties to create copy from
     */
    public ComponentProperties(ComponentProperties other) {
        propertyMap.putAll(other.clone().propertyMap);
    }

    public ComponentProperties() {
    }

    /**
     * Returns component property of this map or empty in case
     * there is no property with such a name or the property
     * has different type.
     *
     * @param key key of the property
     * @param type type of the property
     * @return property
     * @param <T> property type
     */
    public <T extends ComponentProperty<?>> Optional<T> get(String key, Class<T> type) {
        ComponentProperty<?> property = propertyMap.get(key);
        if (!type.isInstance(property)) return Optional.empty();
        return Optional.of(type.cast(property));
    }

    /**
     * Returns component property value of this map or empty in case
     * there is no property with such a name or the property
     * has different type.
     *
     * @param key key of the property
     * @param type type of the property
     * @return property value
     * @param <T> property type
     */
    public <T> Optional<T> getValue(String key, Class<T> type) {
        ComponentProperty<?> property = propertyMap.get(key);
        if (property == null) return Optional.empty();
        Object value = property.value();
        if (!type.isInstance(value)) return Optional.empty();
        return Optional.of(type.cast(value));
    }

    /**
     * Returns component property of this map or the default provided
     * value in case there is no property with such a name or the property
     * has different type.
     *
     * @param key key of the property
     * @param or the default property value. Cannot be null
     * @return property
     * @param <T> property type
     */
    @Contract("_, null -> fail")
    @SuppressWarnings("unchecked")
    public <T extends ComponentProperty<?>> T getOr(String key, T or) {
        ComponentProperty<?> property = propertyMap.get(key);
        if (!or.getClass().isInstance(property)) return or;
        return (T) property;
    }

    /**
     * Returns unwrapped component property of this map or the default provided
     * value in case there is no property with such a name or the property
     * has different type.
     *
     * @param key key of the property
     * @param or the default property value
     * @return property
     * @param <T> property value type
     */
    @Contract("_, null -> fail")
    @SuppressWarnings("unchecked")
    public <T> T getValueOr(String key, T or) {
        ComponentProperty<?> property = propertyMap.get(key);
        if (property == null) return or;
        Object value = property.value();
        if (!or.getClass().isInstance(value)) return or;
        return (T) value;
    }

    /**
     * Returns unwrapped component property of this map if the wrapped property
     * matches the provided class, or empty in case there is no property with
     * given key, or it is different property type.
     *
     * @param key key of the property
     * @param propertyClass type of the property
     * @return unwrapped property value
     * @param <T> value type
     * @param <P> property type
     */
    public <T, P extends ComponentProperty<T>> Optional<T> getAndUnwrap(String key, Class<P> propertyClass) {
        return get(key, propertyClass).map(ComponentProperty::value);
    }

    /**
     * Changes value of this property map.
     *
     * @param key key
     * @param value new value
     * @param <T> property type
     */
    public <T extends ComponentProperty<?>> void set(String key, @Nullable T value) {
        if (value == null) {
            propertyMap.remove(key);
            return;
        }
        if (value.value() == null) throw new IllegalArgumentException();
        propertyMap.put(key, value);
    }

    public void set(String key, String value) {
        if (value == null) {
            set(key, (ComponentProperty<?>) null);
            return;
        }
        set(key, new ComponentProperty.String(value));
    }

    public void set(String key, Boolean value) {
        if (value == null) {
            set(key, (ComponentProperty<?>) null);
            return;
        }
        set(key, new ComponentProperty.Boolean(value));
    }

    public void set(String key, Integer value) {
        if (value == null) {
            set(key, (ComponentProperty<?>) null);
            return;
        }
        set(key, new ComponentProperty.Integer(value));
    }

    public void set(String key, ComponentProperties value) {
        if (value == null) {
            set(key, (ComponentProperty<?>) null);
            return;
        }
        set(key, new ComponentProperty.Properties(value));
    }

    public void set(String key, ComponentProperties[] value) {
        if (value == null) {
            set(key, (ComponentProperty<?>) null);
            return;
        }
        set(key, new ComponentProperty.Array(value));
    }

    public boolean contains(String key) {
        return propertyMap.containsKey(key);
    }

    public void copyAll(ComponentProperties properties) {
        propertyMap.putAll(properties.clone().propertyMap);
    }

    public void clear(ComponentProperties properties) {
        propertyMap.clear();
    }

    public @Unmodifiable Set<String> getKeys() {
        return Collections.unmodifiableSet(propertyMap.keySet());
    }

    public void forEach(BiConsumer<String, ComponentProperty<?>> consumer) {
        propertyMap.forEach(consumer);
    }

    public @UnmodifiableView ComponentProperties unmodifiableView() {
        return this.new View();
    }

    @Override
    public ComponentProperties clone() {
        ComponentProperties clone = new ComponentProperties();
        propertyMap.forEach((key, value) -> clone.propertyMap.put(key, value.clone()));
        return clone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ComponentProperties that)) return false;
        return propertyMap.equals(that.propertyMap);
    }

    @Override
    public int hashCode() {
        return propertyMap.hashCode();
    }

    final class View extends ComponentProperties {

        @Override
        public <T extends ComponentProperty<?>> Optional<T> get(String key, Class<T> type) {
            return ComponentProperties.this.get(key, type);
        }

        @Override
        public <T extends ComponentProperty<?>> T getOr(String key, T or) {
            return ComponentProperties.this.getOr(key, or);
        }

        @Override
        public <T> T getValueOr(String key, T or) {
            return ComponentProperties.this.getValueOr(key, or);
        }

        @Override
        public <T extends ComponentProperty<?>> void set(String key, @Nullable T value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean contains(String key) {
            return ComponentProperties.this.contains(key);
        }

        @Override
        public void copyAll(ComponentProperties properties) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear(ComponentProperties properties) {
            throw new UnsupportedOperationException();
        }

        @Override
        public @Unmodifiable Set<String> getKeys() {
            return ComponentProperties.this.getKeys();
        }

        @Override
        public void forEach(BiConsumer<String, ComponentProperty<?>> consumer) {
            ComponentProperties.this.forEach(consumer);
        }

        @Override
        public @UnmodifiableView ComponentProperties unmodifiableView() {
            return this;
        }

        @Override
        public ComponentProperties clone() {
            return ComponentProperties.this.clone();
        }

        @Override
        public boolean equals(Object o) {
            return ComponentProperties.this.equals(o);
        }

        @Override
        public int hashCode() {
            return ComponentProperties.this.hashCode();
        }

    }

}
