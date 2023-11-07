package org.machinemc.scriptive.events;

import org.jetbrains.annotations.Nullable;
import org.machinemc.scriptive.Contents;
import org.machinemc.scriptive.components.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public record HoverEvent<V extends HoverEvent.Value>(Action<V> action, V contents) implements Contents {

    public HoverEvent(Action<V> action, ValueHolder<V> valueHolder) {
        this(action, valueHolder.asHoverEventValue());
    }

    @Override
    public Map<String, Object> asMap() {
        return Map.of(
                "action", action.name(),
                "contents", contents.asMap()
        );
    }

    public static final class Action<V extends Value> {

        public static final Action<Text> SHOW_TEXT = new Action<>("show_text", Text.class);
        public static final Action<Item> SHOW_ITEM = new Action<>("show_item", Item.class);
        public static final Action<Entity> SHOW_ENTITY = new Action<>("show_entity", Entity.class);

        private final String name;
        private final Class<V> type;

        private Action(String name, Class<V> type) {
            this.name = name;
            this.type = type;
        }

        public String name() {
            return name;
        }

        public Class<V> type() {
            return type;
        }

        @SuppressWarnings("unchecked")
        public static <V extends Value> Action<V> ofValue(V value) {
            return (Action<V>) switch (value) {
                case Text text -> SHOW_TEXT;
                case Item item -> SHOW_ITEM;
                case Entity entity -> SHOW_ENTITY;
            };
        }

    }

    public interface ValueHolder<V extends Value> {

        V asHoverEventValue();

        default HoverEvent<V> asHoverEvent() {
            V value = asHoverEventValue();
            return new HoverEvent<>(Action.ofValue(value), value);
        }

    }

    public sealed interface Value extends Contents permits Text, Item, Entity {}

    public record Text(Component component) implements Value {

        @Override
        public Map<String, Object> asMap() {
            return component().asMap();
        }

    }

    public record Item(String id, int count, @Nullable String tag) implements Value {

        @Override
        public Map<String, Object> asMap() {
            Map<String, Object> map = new HashMap<>();
            map.put("id", id());
            map.put("count", count());
            if (tag() != null) map.put("tag", tag);
            return map;
        }

    }

    public record Entity(UUID id, @Nullable String type, @Nullable String name) implements Value {

        @Override
        public Map<String, Object> asMap() {
            Map<String, Object> map = new HashMap<>();
            map.put("id", id());
            if (type() != null) map.put("type", type());
            if (name() != null) map.put("name", name());
            return map;
        }

    }

}
