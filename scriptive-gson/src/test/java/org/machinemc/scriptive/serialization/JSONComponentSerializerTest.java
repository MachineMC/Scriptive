package org.machinemc.scriptive.serialization;

import org.junit.jupiter.api.Test;
import org.machinemc.scriptive.components.TextComponent;
import org.machinemc.scriptive.components.ClientComponent;
import org.machinemc.scriptive.events.HoverEvent;
import org.machinemc.scriptive.style.ChatColor;

import java.io.IOException;
import java.io.InputStream;

public class JSONComponentSerializerTest {

    @Test
    public void test() {
        ClientComponent component = TextComponent.of("Hello World!")
                .modify()
                .color(ChatColor.BLUE)
                .bold(true)
                .append(" this is a child component")
                .finish();

        JSONComponentSerializer serializer = new JSONComponentSerializer();
        assert serializer.deserialize(serializer.serialize(component)).equals(component);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void hoverEvent() throws IOException {
        String json;
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("component.txt")) {
            assert is != null;
            json = new String(is.readAllBytes());
        }

        JSONComponentSerializer serializer = new JSONComponentSerializer();
        TextComponent component = (TextComponent) serializer.deserialize(json);

        HoverEvent<HoverEvent.Text> hoverEvent = (HoverEvent<HoverEvent.Text>) component.getHoverEvent().orElseThrow();
        HoverEvent.Text content = hoverEvent.contents();

        TextComponent first = (TextComponent) content.component();
        TextComponent second = (TextComponent) content.component().getSiblings().getFirst();

        assert first.getText().equals("This is a hover event");
        assert first.isItalic().orElseThrow();
        assert first.getFont().orElseThrow().equals("minecraft:default");

        assert second.getText().equals("This is another hover text");
    }

}
