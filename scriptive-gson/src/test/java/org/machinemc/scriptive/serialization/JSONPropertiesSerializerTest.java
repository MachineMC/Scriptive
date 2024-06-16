package org.machinemc.scriptive.serialization;

import org.junit.jupiter.api.Test;
import org.machinemc.scriptive.components.TextComponent;
import org.machinemc.scriptive.components.ClientComponent;
import org.machinemc.scriptive.events.HoverEvent;
import org.machinemc.scriptive.style.ChatColor;

import java.io.IOException;
import java.io.InputStream;

public class JSONPropertiesSerializerTest {

    @Test
    public void test() {
        ClientComponent component = TextComponent.of("Hello World!")
                .modify()
                .color(ChatColor.BLUE)
                .bold(true)
                .append(" this is a child component")
                .finish();

        ComponentSerializer componentSerializer = new ComponentSerializer();
        JSONPropertiesSerializer propertiesSerializer = new JSONPropertiesSerializer();
        String serialized = componentSerializer.serialize(component, propertiesSerializer);
        assert componentSerializer.deserialize(serialized, propertiesSerializer).equals(component);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void hoverEvent() throws IOException {
        String json;
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("component.txt")) {
            assert is != null;
            json = new String(is.readAllBytes());
        }

        ComponentSerializer componentSerializer = new ComponentSerializer();
        JSONPropertiesSerializer propertiesSerializer = new JSONPropertiesSerializer();
        TextComponent component = (TextComponent) componentSerializer.deserialize(json, propertiesSerializer);

        HoverEvent<HoverEvent.Text> hoverEvent = (HoverEvent<HoverEvent.Text>) component.getHoverEvent().orElseThrow();
        HoverEvent.Text content = hoverEvent.contents();

        TextComponent first = (TextComponent) content.component();
        TextComponent second = (TextComponent) first.getSiblings().getFirst();

        assert first.getText().equals("This is a hover event");
        assert first.isItalic().orElseThrow();
        assert first.getFont().orElseThrow().equals("minecraft:default");

        assert second.getText().equals("This is another hover text");
    }

}
