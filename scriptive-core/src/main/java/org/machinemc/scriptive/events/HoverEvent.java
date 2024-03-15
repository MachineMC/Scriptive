package org.machinemc.scriptive.events;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.machinemc.scriptive.Contents;
import org.machinemc.scriptive.components.Component;
import org.machinemc.scriptive.components.TextComponent;
import org.machinemc.scriptive.serialization.MapComponentSerializer;

import java.util.*;

public record HoverEvent<V extends HoverEvent.Value<?>>(Action<V> action, V contents) implements Contents {

    public HoverEvent(Action<V> action, ValueHolder<V> valueHolder) {
        this(action, valueHolder.asHoverEventValue());
    }

    public HoverEvent<V> withValue(V value) {
        return new HoverEvent<>(action, value);
    }

    public HoverEvent<V> withValue(ValueHolder<V> valueHolder) {
        return new HoverEvent<>(action, valueHolder);
    }

    @Override
    public Map<String, Object> asMap() {
        return Map.of(
                "action", action.name(),
                "contents", contents.getContents()
        );
    }

    @SuppressWarnings("unchecked")
    public static <V extends Value<?>> HoverEvent<?> deserialize(Map<String, Object> map) {
        HoverEvent.Action<V> action = (Action<V>) Action.byName(map.get("action") + "");
        if (action == null) return null;
        return new HoverEvent<>(action, action.parseValue(map.get("contents")));
    }

    public static final class Action<V extends Value<?>> {

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
        public V parseValue(Object value) {
            if (value == null || (this != SHOW_TEXT && !(value instanceof Map<?, ?>))) return null;

            if (this == SHOW_TEXT) {
                return (V) switch (value) {
                    case String string -> new Text(TextComponent.of(string));
                    case Map<?, ?> map -> new Text(MapComponentSerializer.get().deserialize((Map<String, Object>) map));
                    case List<?> list -> {
                        List<Component> components = new ArrayList<>();
                        for (Object o : list) {
                            if (o instanceof String s) {
                                components.add(TextComponent.of(s));
                                continue;
                            }
                            components.add(MapComponentSerializer.get().deserialize((Map<String, Object>) o));
                        }
                        yield new Text(components);
                    }
                    default -> null;
                };
            }

            else if (this == SHOW_ITEM) {
                Map<String, Object> map = (Map<String, Object>) value;
                if (!map.containsKey("id") || !map.containsKey("count")) return null;
                String id = map.get("id") + "";
                int count = Integer.parseInt(map.get("count") + "");
                String tag = map.containsKey("tag") ? map.get("tag") + "" : null;
                return (V) new Item(id, count, tag);
            }

            else if (this == SHOW_ENTITY) {
                Map<String, Object> map = (Map<String, Object>) value;
                UUID id = map.containsKey("id") ? UUID.fromString(map.get("id") + "") : null;
                if (id == null) return null;
                String type = map.containsKey("type") ? map.get("type") + "" : null;
                String name = map.containsKey("name") ? map.get("name") + "" : null;
                return (V) new Entity(id, type, name);
            }

            throw new UnsupportedOperationException();
        }

        public static Action<?> byName(String name) {
            return switch (name) {
                case "show_text" -> SHOW_TEXT;
                case "show_item" -> SHOW_ITEM;
                case "show_entity" -> SHOW_ENTITY;
                default -> null;
            };
        }

        @SuppressWarnings("unchecked")
        public static <V extends Value<?>> Action<V> ofValue(V value) {
            return (Action<V>) switch (value) {
                case Text text -> SHOW_TEXT;
                case Item item -> SHOW_ITEM;
                case Entity entity -> SHOW_ENTITY;
            };
        }

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

    public interface ValueHolder<V extends Value<?>> {

        V asHoverEventValue();

        default HoverEvent<V> asHoverEvent() {
            V value = asHoverEventValue();
            return new HoverEvent<>(Action.ofValue(value), value);
        }

    }

    public sealed interface Value<T> permits Text, Item, Entity {

        T getContents();

    }

    public record Text(@Unmodifiable List<Component> components) implements Value<List<Map<String, Object>>> {

        public Text(Collection<Component> components) {
            this(new ArrayList<>(components));
        }

        public Text(Component component) {
            this(Collections.singletonList(component));
        }

        public Text {
            components = Collections.unmodifiableList(components);
        }

        @Override
        public @Unmodifiable List<Map<String, Object>> getContents() {
            List<Map<String, Object>> list = new ArrayList<>();
            components.forEach(component -> list.add(component.asMap()));
            return Collections.unmodifiableList(list);
        }

    }

    public record Item(String id, int count, @Nullable String tag) implements Value<Map<String, Object>> {

        @Override
        public Map<String, Object> getContents() {
            Map<String, Object> map = new HashMap<>();
            map.put("id", id());
            map.put("count", count());
            if (tag() != null) map.put("tag", tag);
            return map;
        }

    }

    public record Entity(UUID id, @Nullable String type, @Nullable String name) implements Value<Map<String, Object>> {

        @Override
        public Map<String, Object> getContents() {
            Map<String, Object> map = new HashMap<>();
            map.put("id", id());
            if (type() != null) map.put("type", type());
            if (name() != null) map.put("name", name());
            return map;
        }

    }

}
