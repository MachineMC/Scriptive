package org.machinemc.scriptive.serialization;

import org.junit.jupiter.api.Test;
import org.machinemc.scriptive.components.Component;
import org.machinemc.scriptive.components.TextComponent;
import org.machinemc.scriptive.style.ChatColor;

import java.util.Map;

public class MapPropertiesSerializerTest {

    @Test
    public void simpleTest() {
        TextComponent component = TextComponent.of("Hello World!")
                .modify()
                .color(ChatColor.BLUE)
                .italic(true)
                .finish();
        TextComponent sibling = TextComponent.of("I am a child component");
        component.append(sibling);

        ComponentSerializer componentSerializer = new ComponentSerializer();
        MapPropertiesSerializer propertiesSerializer = MapPropertiesSerializer.get();
        Map<String, ?> map = componentSerializer.serialize(component, propertiesSerializer);

        Component deserialized = componentSerializer.deserialize(map, propertiesSerializer);

        assert component.equals(deserialized);
    }

}
