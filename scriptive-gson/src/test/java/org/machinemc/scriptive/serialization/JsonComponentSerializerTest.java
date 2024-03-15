package org.machinemc.scriptive.serialization;

import org.junit.jupiter.api.Test;
import org.machinemc.scriptive.components.TextComponent;
import org.machinemc.scriptive.components.VanillaComponent;
import org.machinemc.scriptive.events.HoverEvent;
import org.machinemc.scriptive.style.ChatColor;

import java.io.IOException;
import java.io.InputStream;

public class JsonComponentSerializerTest {

    @Test
    public void test() {
        VanillaComponent component = TextComponent.of("Hello World!")
                .modify()
                .color(ChatColor.BLUE)
                .bold(true)
                .append(" this is a child component")
                .finish();

        JsonComponentSerializer serializer = JsonComponentSerializer.get();
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

        JsonComponentSerializer serializer = JsonComponentSerializer.get();
        TextComponent component = (TextComponent) serializer.deserialize(json);

        HoverEvent<HoverEvent.Text> hoverEvent = (HoverEvent<HoverEvent.Text>) component.getHoverEvent().orElseThrow();
        HoverEvent.Text content = hoverEvent.contents();

        TextComponent first = (TextComponent) content.components().getFirst();
        TextComponent second = (TextComponent) content.components().getLast();

        assert first.getText().equals("This is a hover event");
        assert first.isItalic().orElseThrow();
        assert first.getFont().orElseThrow().equals("minecraft:default");

        assert second.getText().equals("This is another hover text");
    }

}
