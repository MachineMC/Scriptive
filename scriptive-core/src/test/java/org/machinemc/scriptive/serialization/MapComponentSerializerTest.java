package org.machinemc.scriptive.serialization;

import org.junit.jupiter.api.Test;
import org.machinemc.scriptive.components.Component;
import org.machinemc.scriptive.components.TextComponent;
import org.machinemc.scriptive.style.ChatColor;

import java.util.Map;

public class MapComponentSerializerTest {

    @Test
    public void simpleTest() {
        TextComponent component = TextComponent.of("Hello World!")
                .modify()
                .color(ChatColor.BLUE)
                .italic(true)
                .finish();
        TextComponent sibling = TextComponent.of("I am a child component");
        component.append(sibling);

        MapComponentSerializer serializer = new MapComponentSerializer();
        Map<String, ?> map = serializer.serialize(component);

        Component deserialized = serializer.deserialize(map);

        assert component.equals(deserialized);
    }

}
