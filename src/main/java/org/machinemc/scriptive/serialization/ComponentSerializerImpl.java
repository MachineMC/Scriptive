package org.machinemc.scriptive.serialization;

import org.machinemc.scriptive.components.Component;
import org.machinemc.scriptive.components.KeybindComponent;
import org.machinemc.scriptive.components.TextComponent;
import org.machinemc.scriptive.components.TranslationComponent;
import org.machinemc.scriptive.events.ClickEvent;
import org.machinemc.scriptive.events.HoverEvent;
import org.machinemc.scriptive.style.ChatColor;
import org.machinemc.scriptive.style.HexColor;

import java.util.*;

@SuppressWarnings("unchecked")
public class ComponentSerializerImpl implements ComponentSerializer {

    private final Map<String[], Class<? extends Component>> componentKeysMap = new HashMap<>();
    private final Map<Class<? extends Component>, ComponentCreator<?>> componentCreators = new HashMap<>();

    {
        register(KeybindComponent.class, properties -> KeybindComponent.of((String) properties.get("keybind")), "keybind");
        register(TextComponent.class, properties -> TextComponent.of((String) properties.get("text")), "text");
        register(TranslationComponent.class, properties -> {
            String translate = (String) properties.get("translate");
            List<Map<String, Object>> argumentsMap = (List<Map<String, Object>>) properties.getOrDefault("with", new ArrayList<>());
            Component[] arguments = new Component[argumentsMap.size()];
            int index = 0;
            for (Map<String, Object> map : argumentsMap)
                arguments[index++] = deserialize(map);
            return TranslationComponent.of(translate, arguments);
        }, "translate", "with");
    }

    @Override
    public <T extends Component> void register(Class<T> type, ComponentCreator<T> creator, String... uniqueKeys) {
        if (componentCreators.containsKey(type))
            throw new IllegalArgumentException("Type '" + type + "' is already registered");
        componentKeysMap.put(uniqueKeys, type);
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

        Optional.ofNullable(map.get("bold"))
                .filter(o -> o instanceof Boolean)
                .map(o -> (Boolean) o)
                .ifPresent(component::setBold);
        Optional.ofNullable(map.get("italic"))
                .filter(o -> o instanceof Boolean)
                .map(o -> (Boolean) o)
                .ifPresent(component::setItalic);
        Optional.ofNullable(map.get("underlined"))
                .filter(o -> o instanceof Boolean)
                .map(o -> (Boolean) o)
                .ifPresent(component::setUnderlined);
        Optional.ofNullable(map.get("strikethrough"))
                .filter(o -> o instanceof Boolean)
                .map(o -> (Boolean) o)
                .ifPresent(component::setStrikethrough);
        Optional.ofNullable(map.get("obfuscated"))
                .filter(o -> o instanceof Boolean)
                .map(o -> (Boolean) o)
                .ifPresent(component::setObfuscated);

        Optional.ofNullable(map.get("insertion"))
                .map(String::valueOf)
                .ifPresent(component::setInsertion);

        // TODO fix deserialization of these two
        Optional.ofNullable(map.get("clickEvent"))
                .filter(o -> o instanceof ClickEvent)
                .map(o -> (ClickEvent) o)
                .ifPresent(component::setClickEvent);
        Optional.ofNullable(map.get("hoverEvent"))
                .filter(o -> o instanceof HoverEvent<?>)
                .map(o -> (HoverEvent<?>) o)
                .ifPresent(component::setHoverEvent);

        if (map.containsKey("extra")) {
            component.clearSiblings();
            for (Map<String, Object> extra : ((List<Map<String, Object>>) map.get("extra")))
                component.append((Component) deserialize(extra));
        }

        return component;
    }

    public Class<? extends Component> getComponentTypeFromMap(Map<String, Object> properties) {
        for (Map.Entry<String[], Class<? extends Component>> entry : componentKeysMap.entrySet()) {
            for (String key : entry.getKey()) {
                if (properties.containsKey(key))
                    return entry.getValue();
            }
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
        Class<T> type = (Class<T>) getComponentTypeFromMap(properties);
        if (type == null)
            throw new IllegalArgumentException("Don't know how to turn " + properties + " into a component");
        return newInstance(type, properties);
    }

    public <T extends Component> T newInstance(Class<T> type, Map<String, Object> properties) {
        return getCreator(type).create(properties);
    }

}
