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

public class ComponentSerializerImpl implements ComponentSerializer {

    private final Map<String[], Class<? extends Component>> componentKeysMap = new HashMap<>();
    private final Map<Class<? extends Component>, ComponentCreator<?>> componentCreators = new HashMap<>();

    {
        register(KeybindComponent.class, properties -> KeybindComponent.of((String) properties.get("keybind")), "keybind");
        register(TextComponent.class, properties -> TextComponent.of((String) properties.get("text")), "text");
        register(TranslationComponent.class, properties -> {
            String translate = (String) properties.get("translate");
            //noinspection unchecked
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
    @SuppressWarnings("unchecked")
    public <C extends Component> C deserialize(Map<String, Object> map) {
        C component = newInstance(map);

        if (map.containsKey("color")) {
            String color = (String) map.get("color");
            if (color != null)
                component.setColor(color.startsWith("#") ? new HexColor(color) : ChatColor.valueOf(color.toUpperCase(Locale.ENGLISH)));
        }

        component.setBold((Boolean) map.getOrDefault("bold", component.isBold().orElse(null)));
        component.setItalic((Boolean) map.getOrDefault("italic", component.isItalic().orElse(null)));
        component.setUnderlined((Boolean) map.getOrDefault("underlined", component.isUnderlined().orElse(null)));
        component.setStrikethrough((Boolean) map.getOrDefault("strikethrough", component.isStrikethrough().orElse(null)));
        component.setObfuscated((Boolean) map.getOrDefault("obfuscated", component.isObfuscated().orElse(null)));
        component.setInsertion((String) map.getOrDefault("insertion", component.getInsertion().orElse(null)));
        component.setClickEvent((ClickEvent) map.getOrDefault("clickEvent", component.getClickEvent().orElse(null)));
        component.setHoverEvent((HoverEvent) map.getOrDefault("hoverEvent", component.getHoverEvent().orElse(null)));

        component.clearSiblings();
        if (map.containsKey("extra")) {
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

    @SuppressWarnings("unchecked")
    public <T extends Component> ComponentCreator<T> getCreator(Class<T> type) {
        ComponentCreator<T> creator = (ComponentCreator<T>) componentCreators.get(type);
        if (creator == null)
            throw new IllegalArgumentException("Type '" + type + "' does not have a registered ComponentCreator");
        return creator;
    }

    @SuppressWarnings("unchecked")
    public <T extends Component> T newInstance(Map<String, Object> properties) {
        Class<T> type = (Class<T>) getComponentTypeFromMap(properties);
        if (type == null)
            throw new IllegalArgumentException("Ambiguous map, couldn't deserialize");
        return newInstance(type, properties);
    }

    public <T extends Component> T newInstance(Class<T> type, Map<String, Object> properties) {
        return getCreator(type).create(properties);
    }

}
