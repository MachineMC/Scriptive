package org.machinemc.scriptive.serialization;

import org.jetbrains.annotations.Nullable;
import org.machinemc.scriptive.components.*;
import org.machinemc.scriptive.events.ClickEvent;
import org.machinemc.scriptive.events.HoverEvent;
import org.machinemc.scriptive.style.ChatColor;
import org.machinemc.scriptive.style.HexColor;

import java.util.*;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public class MapComponentSerializer implements ComponentSerializer<Map<String, Object>> {

    private static final MapComponentSerializer INSTANCE = new MapComponentSerializer();

    private final Map<String, Class<? extends ClientComponent>> componentKeysMap = new HashMap<>();
    private final Map<Class<? extends ClientComponent>, ComponentCreator<?>> componentCreators = new LinkedHashMap<>();

    {
        register(KeybindComponent.class, properties -> KeybindComponent.of((String) properties.get("keybind")), "keybind");
        register(TextComponent.class, properties -> TextComponent.of((String) properties.get("text")), "text");
        register(TranslationComponent.class, properties -> {
            String translate = (String) properties.get("translate");
            List<?> argumentsMap = (List<?>) properties.getOrDefault("with", new ArrayList<>());
            Component[] arguments = new Component[argumentsMap.size()];
            for (int i = 0; i < argumentsMap.size(); i++)
                arguments[i] = ObjectComponentSerializer.get().deserialize(argumentsMap.get(i));
            return TranslationComponent.of(translate, arguments);
        }, "translate", "with");
    }

    public static MapComponentSerializer get() {
        return INSTANCE;
    }

    private MapComponentSerializer() {
    }

    private <T extends ClientComponent> void register(Class<T> type, ComponentCreator<T> creator, String... uniqueKeys) {
        if (componentCreators.containsKey(type))
            throw new IllegalArgumentException("Type '" + type + "' is already registered");
        for (String key : uniqueKeys)
            componentKeysMap.putIfAbsent(key, type);
        componentCreators.put(type, creator);
    }

    private Class<? extends ClientComponent> getComponentTypeFromProperties(Map<String, Object> properties) {
        for (String key : properties.keySet()) {
            Class<? extends ClientComponent> type = componentKeysMap.get(key);
            if (type != null) return type;
        }
        return null;
    }

    private <T extends ClientComponent> ComponentCreator<T> getCreator(Class<T> type) {
        ComponentCreator<T> creator = (ComponentCreator<T>) componentCreators.get(type);
        if (creator == null)
            throw new IllegalArgumentException("Type '" + type + "' does not have a registered ComponentCreator");
        return creator;
    }

    private <T extends ClientComponent> T newInstance(Map<String, Object> properties) {
        Class<T> type = (Class<T>) getComponentTypeFromProperties(properties);
        if (type == null)
            throw new IllegalArgumentException("Don't know how to turn " + properties + " into a component");
        return newInstance(type, properties);
    }

    private <T extends ClientComponent> T newInstance(Class<T> type, Map<String, Object> properties) {
        return getCreator(type).create(properties);
    }

    private static <T> void deserializePart(@Nullable Object part, Class<T> expected, Consumer<T> consumer) {
        Optional.ofNullable(part)
                .filter(expected::isInstance)
                .map(expected::cast)
                .ifPresent(consumer);
    }

    @Override
    public ClientComponent deserialize(Map<String, Object> input) {
        ClientComponent component = newInstance(input);

        Optional.ofNullable(input.get("color"))
                .map(String::valueOf)
                .flatMap(color -> color.startsWith("#") ? HexColor.of(color) : ChatColor.byName(color))
                .ifPresent(component::setColor);

        deserializePart(input.get("bold"), Boolean.class, component::setBold);
        deserializePart(input.get("italic"), Boolean.class, component::setItalic);
        deserializePart(input.get("underlined"), Boolean.class, component::setUnderlined);
        deserializePart(input.get("strikethrough"), Boolean.class, component::setStrikethrough);
        deserializePart(input.get("obfuscated"), Boolean.class, component::setObfuscated);
        deserializePart(input.get("insertion"), String.class, component::setInsertion);
        deserializePart(input.get("font"), String.class, component::setFont);

        deserializePart(
                input.get("clickEvent"),
                Map.class,
                properties -> component.setClickEvent(ClickEvent.deserialize(properties))
        );
        deserializePart(
                input.get("hoverEvent"),
                Map.class,
                properties -> component.setHoverEvent(HoverEvent.deserialize(properties))
        );

        List<Map<String, Object>> extra = (List<Map<String, Object>>) input.get("extra");
        if (extra != null) {
            for (Map<String, Object> child : extra)
                component.append(ObjectComponentSerializer.get().deserialize(child));
        }

        return component;
    }

    @Override
    public Map<String, Object> serialize(ClientComponent component) {
        return component.asMap();
    }

    @FunctionalInterface
    private interface ComponentCreator<C extends Component> {

        C create(Map<String, Object> properties);

    }

}
