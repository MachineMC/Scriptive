package org.machinemc.scriptive.events;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import org.machinemc.scriptive.serialization.ComponentProperties;
import org.machinemc.scriptive.serialization.ComponentProperty;
import org.machinemc.scriptive.serialization.ComponentSerializer;
import org.machinemc.scriptive.serialization.Contents;
import org.machinemc.scriptive.components.Component;

import java.util.*;

/**
 * Represents an event that happens when client hovers over a component.
 *
 * @param action action
 * @param contents content of the action
 */
public record HoverEvent<V extends HoverEvent.Value>(Action<V> action, V contents) implements Contents {

    /**
     * Hover event that shows text on hover.
     */
    public static final Action<Text> SHOW_TEXT = new Action<>("show_text", Text.class);

    /**
     * Hover event that shows an item on hover.
     */
    public static final Action<Item> SHOW_ITEM = new Action<>("show_item", Item.class);

    /**
     * Hover event that show information about an entity on hover.
     */
    public static final Action<Entity> SHOW_ENTITY = new Action<>("show_entity", Entity.class);

    public HoverEvent {
        Objects.requireNonNull(action, "Action can not be null");
        Objects.requireNonNull(contents, "Contents can not be null");
    }

    public HoverEvent(Action<V> action, ValueHolder<V> valueHolder) {
        this(action, valueHolder.asHoverEventValue());
    }

    /**
     * Creates hover event from component properties.
     *
     * @param properties component properties
     * @return hover event
     * @param <V> hover event value type
     */
    @SuppressWarnings("unchecked")
    public static <V extends HoverEvent.Value> Optional<HoverEvent<V>> fromProperties(ComponentProperties properties,
                                                                                      ComponentSerializer serializer) {
        if (!properties.contains("action")) return Optional.empty();
        String actionName = properties.getValue("action", String.class).orElse(null);
        if (actionName == null) return Optional.empty();
        Action<V> action = (Action<V>) Action.byName(actionName);
        if (action == null) return Optional.empty();

        if (action == SHOW_TEXT) {
            ComponentProperty<?> property = properties.get("contents", ComponentProperty.class).orElse(null);
            if (property == null) return Optional.empty();
            return Optional.of(new HoverEvent<>(action, (V) new Text(ComponentProperty.convertToProperties(property).value(), serializer)));
        }

        if (action == SHOW_ITEM) {
            ComponentProperties contents = properties.getValue("contents", ComponentProperties.class).orElse(null);
            if (contents == null) return Optional.empty();
            return Optional.of(new HoverEvent<>(action, (V) new Item(contents)));
        }

        if (action == SHOW_ENTITY) {
            ComponentProperties contents = properties.getValue("contents", ComponentProperties.class).orElse(null);
            if (contents == null) return Optional.empty();
            return Optional.of(new HoverEvent<>(action, (V) new Entity(contents, serializer)));
        }

        return Optional.empty();
    }

    @Override
    public @UnmodifiableView ComponentProperties getProperties() {
        ComponentProperties properties = new ComponentProperties();
        properties.set("action", action.name());
        properties.set("contents", contents.getProperties());
        return properties.unmodifiableView();
    }

    public HoverEvent<V> withValue(V value) {
        return new HoverEvent<>(action, value);
    }

    public HoverEvent<V> withValue(ValueHolder<V> valueHolder) {
        return new HoverEvent<>(action, valueHolder);
    }

    /**
     * Different actions that may happen when client
     * hovers over the component.
     *
     * @param <V> value type
     */
    public static final class Action<V extends Value> {

        private final String name;
        private final Class<V> type;

        private Action(String name, Class<V> type) {
            this.name = Objects.requireNonNull(name, "Name can not be null");
            this.type = Objects.requireNonNull(type, "Type can not be null");
        }

        /**
         * @return name of the action
         */
        public String name() {
            return name;
        }

        /**
         * @return value type of the action
         */
        public Class<V> type() {
            return type;
        }

        /**
         * Returns action by its name.
         *
         * @param name name of the action
         * @return action
         */
        public static @Nullable Action<?> byName(String name) {
            return switch (name.toLowerCase()) {
                case "show_text" -> SHOW_TEXT;
                case "show_item" -> SHOW_ITEM;
                case "show_entity" -> SHOW_ENTITY;
                default -> null;
            };
        }

        /**
         * Returns action for given value.
         *
         * @param value value
         * @return action for given value
         * @param <V> value type
         */
        @SuppressWarnings("unchecked")
        public static <V extends Value> Action<V> ofValue(V value) {
            return (Action<V>) switch (value) {
                case Text text -> SHOW_TEXT;
                case Item item -> SHOW_ITEM;
                case Entity entity -> SHOW_ENTITY;
            };
        }

        /**
         * @return all possible action values
         */
        public static Action<?>[] values() {
            return new Action[]{SHOW_TEXT, SHOW_ITEM, SHOW_ENTITY};
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Action<?> action)) return false;
            return name.equals(action.name) && type.equals(action.type);
        }

        @Override
        public int hashCode() {
            int result = name.hashCode();
            result = 31 * result + type.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", "Action[", "]")
                    .add("name=" + name)
                    .add("type=" + type.getSimpleName())
                    .toString();
        }

    }

    /**
     * Object that, as itself, can be represented as a value for
     * hover event.
     *
     * @param <V> value type
     */
    public interface ValueHolder<V extends Value> {

        /**
         * @return hover value representing this object
         */
        V asHoverEventValue();

        /**
         * @return hover event representing this object
         */
        default HoverEvent<V> asHoverEvent() {
            V value = asHoverEventValue();
            return new HoverEvent<>(Action.ofValue(value), value);
        }

    }

    /**
     * Represents a value for a hover event.
     */
    public sealed interface Value extends Contents permits Text, Item, Entity {
    }

    /**
     * Text hover value.
     *
     * @param component component
     */
    public record Text(Component component) implements Value {

        public Text {
            Objects.requireNonNull(component, "Component can not be null");
        }

        public Text(ComponentProperties componentProperties, ComponentSerializer serializer) {
            this(serializer.deserialize(componentProperties));
        }

        @Override
        public @UnmodifiableView ComponentProperties getProperties() {
            return component.getProperties().unmodifiableView();
        }

    }

    /**
     * Item hover value.
     *
     * @param id namespaced key of the item
     * @param count count of the item
     * @param tag NBT of this item in the SNBT format
     */
    public record Item(String id, int count, @Nullable String tag) implements Value {

        public Item {
            Objects.requireNonNull(id, "Item ID can not be null");
        }

        public Item(ComponentProperties properties) {
            this(
                    properties.getValue("id", String.class).orElseThrow(),
                    properties.getValue("count", Integer.class).orElseThrow(),
                    properties.getValue("tag", String.class).orElse(null)
            );
        }

        @Override
        public @UnmodifiableView ComponentProperties getProperties() {
            ComponentProperties properties = new ComponentProperties();
            properties.set("id", id);
            properties.set("count", count);
            properties.set("tag", tag);
            return properties.unmodifiableView();
        }

    }

    /**
     * Entity hover value.
     *
     * @param id UUID of the entity
     * @param type namespaced key of type of the entity, defaults to {@code minecraft:pig} on the client
     *             if this value is not specified
     * @param name custom name of the entity
     */
    public record Entity(UUID id, @Nullable String type, @Nullable Component name) implements Value {

        public Entity {
            Objects.requireNonNull(id, "Entity UUID can not be null");
        }

        public Entity(ComponentProperties properties, ComponentSerializer serializer) {
            this(
                    UUID.fromString(properties.getValue("id", String.class).orElseThrow()),
                    properties.getValue("type", String.class).orElse(null),
                    properties.getValue("name", ComponentProperties.class).map(serializer::deserialize).orElse(null)
            );
        }

        @Override
        public @UnmodifiableView ComponentProperties getProperties() {
            ComponentProperties properties = new ComponentProperties();
            properties.set("id", id.toString());
            properties.set("type", type);
            if (name != null)
                properties.set("name", name.getProperties());
            return properties.unmodifiableView();
        }

    }

}
