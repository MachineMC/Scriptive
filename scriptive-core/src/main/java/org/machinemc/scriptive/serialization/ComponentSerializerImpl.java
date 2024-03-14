package org.machinemc.scriptive.serialization;

import org.jetbrains.annotations.Nullable;
import org.machinemc.scriptive.components.Component;
import org.machinemc.scriptive.components.KeybindComponent;
import org.machinemc.scriptive.components.TextComponent;
import org.machinemc.scriptive.components.TranslationComponent;
import org.machinemc.scriptive.events.ClickEvent;
import org.machinemc.scriptive.events.HoverEvent;
import org.machinemc.scriptive.style.ChatColor;
import org.machinemc.scriptive.style.HexColor;

import java.util.*;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
class ComponentSerializerImpl implements ComponentSerializer {

    private final Map<String, Class<? extends Component>> componentKeysMap = new HashMap<>();
    private final Map<Class<? extends Component>, ComponentCreator<?>> componentCreators = new LinkedHashMap<>();

    {
        register(KeybindComponent.class, properties -> KeybindComponent.of((String) properties.get("keybind")), "keybind");
        register(TextComponent.class, properties -> TextComponent.of((String) properties.get("text")), "text");
        register(TranslationComponent.class, properties -> {
            String translate = (String) properties.get("translate");
            List<Map<String, Object>> argumentsMap = (List<Map<String, Object>>) properties.getOrDefault("with", new ArrayList<>());
            Component[] arguments = new Component[argumentsMap.size()];
            for (int i = 0; i < argumentsMap.size(); i++)
                arguments[i] = deserialize(argumentsMap.get(i));
            return TranslationComponent.of(translate, arguments);
        }, "translate", "with");
    }

    @Override
    public <T extends Component> void register(Class<T> type, ComponentCreator<T> creator, String... uniqueKeys) {
        if (componentCreators.containsKey(type))
            throw new IllegalArgumentException("Type '" + type + "' is already registered");
        for (String key : uniqueKeys)
            componentKeysMap.putIfAbsent(key, type);
        componentCreators.put(type, creator);
    }

    @Override
    public Map<String, Object> serialize(Component component) {
        return component.asMap();
    }

    @Override
    public <C extends Component> C deserialize(Map<String, Object> map) {
        C component = newInstance(map);

        Optional.ofNullable(map.get("color"))
                .map(String::valueOf)
                .flatMap(color -> color.startsWith("#") ? HexColor.of(color) : ChatColor.byName(color))
                .ifPresent(component::setColor);

        deserializePart(map.get("bold"), Boolean.class, component::setBold);
        deserializePart(map.get("italic"), Boolean.class, component::setItalic);
        deserializePart(map.get("underlined"), Boolean.class, component::setUnderlined);
        deserializePart(map.get("strikethrough"), Boolean.class, component::setStrikethrough);
        deserializePart(map.get("obfuscated"), Boolean.class, component::setObfuscated);
        deserializePart(map.get("insertion"), String.class, component::setInsertion);

        deserializePart(
                map.get("clickEvent"),
                Map.class,
                properties -> component.setClickEvent(ClickEvent.deserialize(properties))
        );
        deserializePart(
                map.get("hoverEvent"),
                Map.class,
                properties -> component.setHoverEvent(HoverEvent.deserialize(this, properties))
        );

        if (map.containsKey("extra")) {
            component.clearSiblings();
            for (Map<String, Object> extra : ((List<Map<String, Object>>) map.get("extra")))
                component.append((Component) deserialize(extra));
        }

        return component;
    }

    public Class<? extends Component> getComponentTypeFromProperties(Map<String, Object> properties) {
        for (String key : properties.keySet()) {
            Class<? extends Component> type = componentKeysMap.get(key);
            if (type != null) return type;
        }
        return null;
    }

    public <T extends Component> ComponentCreator<T> getCreator(Class<T> type) {
        ComponentCreator<T> creator = (ComponentCreator<T>) componentCreators.get(type);
        if (creator == null)
            throw new IllegalArgumentException("Type '" + type + "' does not have a registered ComponentCreator");
        return creator;
    }

    public <T extends Component> T newInstance(Map<String, Object> properties) {
        Class<T> type = (Class<T>) getComponentTypeFromProperties(properties);
        if (type == null)
            throw new IllegalArgumentException("Don't know how to turn " + properties + " into a component");
        return newInstance(type, properties);
    }

    public <T extends Component> T newInstance(Class<T> type, Map<String, Object> properties) {
        return getCreator(type).create(properties);
    }

    private static <T> void deserializePart(@Nullable Object part, Class<T> expected, Consumer<T> consumer) {
        Optional.ofNullable(part)
                .filter(expected::isInstance)
                .map(expected::cast)
                .ifPresent(consumer);
    }

}
