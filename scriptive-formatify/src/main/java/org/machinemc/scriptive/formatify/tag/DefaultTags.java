package org.machinemc.scriptive.formatify.tag;

import org.machinemc.scriptive.components.Component;
import org.machinemc.scriptive.components.KeybindComponent;
import org.machinemc.scriptive.components.TranslationComponent;
import org.machinemc.scriptive.events.ClickEvent;
import org.machinemc.scriptive.events.HoverEvent;
import org.machinemc.scriptive.formatify.exceptions.ParseException;
import org.machinemc.scriptive.formatify.parameter.ColorParameter;
import org.machinemc.scriptive.formatify.parameter.EnumParameter;
import org.machinemc.scriptive.formatify.parameter.IntegerParameter;
import org.machinemc.scriptive.formatify.parameter.StringParameter;
import org.machinemc.scriptive.style.Colour;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface DefaultTags {

    TagResolver BOLD = TagResolver.resolver(Set.of("bold", "b"), component -> component.setBold(true));
    TagResolver OBFUSCATED = TagResolver.resolver(
        Set.of("obfuscated", "obf", "magic"),
        component -> component.setObfuscated(true)
    );
    TagResolver ITALIC = TagResolver.resolver(
        Set.of("italic", "italics", "i"),
        component -> component.setItalic(true)
    );
    TagResolver UNDERLINED = TagResolver.resolver(
        Set.of("underlined", "underline", "u"),
        component -> component.setUnderlined(true)
    );
    TagResolver STRIKETHROUGH = TagResolver.resolver(
        Set.of("strikethrough", "strike"),
        component -> component.setStrikethrough(true)
    );
    TagResolver FONT = TagResolver.builder()
        .name("font")
        .parameter("font", new StringParameter())
        .componentUpdater((component, arguments) -> component.setFont(arguments.get("font")))
        .build();
    TagResolver INSERTION = TagResolver.builder()
        .names("insertion", "insert")
        .parameter("insertion", new StringParameter())
        .componentUpdater((component, arguments) -> component.setInsertion(arguments.get("insertion")))
        .build();
    TagResolver CLICK_EVENT = TagResolver.builder()
        .names("click", "click_event")
        .parameter("action", new EnumParameter<>(ClickEvent.Action.class))
        .parameter("value", new StringParameter())
        .componentUpdater((component, arguments) -> {
            ClickEvent.Action action = arguments.get("action");
            String value = arguments.get("value");
            component.setClickEvent(new ClickEvent(action, value));
        })
        .build();
    TagResolver HOVER_EVENT = (tagName, arguments) -> {
        if (!tagName.equals("hover") && !tagName.equals("hover_event")) return Optional.empty();
        String actionName = arguments.pollOr("'action' is not specified");
        HoverEvent.Action<?> action = HoverEvent.Action.byName(actionName);
        if (action == null) throw new ParseException("'" + actionName + "' is not a valid hover action");
        HoverEvent<?> hoverEvent;
        if (action == HoverEvent.SHOW_TEXT) {
            Component value = arguments.pollOr(arguments.formatify()::parse, "'value' is not specified");
            //noinspection unchecked
            hoverEvent = new HoverEvent<>((HoverEvent.Action<HoverEvent.Text>) action, value);
        } else if (action == HoverEvent.SHOW_ITEM) {
            String id = arguments.pollOr("'id' is not specified");
            int amount = arguments.pollOrDefault(new IntegerParameter()::parse, 1);
            String tag = arguments.poll();
            //noinspection unchecked
            hoverEvent = new HoverEvent<>(
                (HoverEvent.Action<HoverEvent.Item>) action,
                new HoverEvent.Item(id, amount, tag)
            );
        } else {
            UUID id = arguments.pollOr(UUID::fromString, "'id' is not specified");
            String type = arguments.poll();
            String name = arguments.poll();
            //noinspection unchecked
            hoverEvent = new HoverEvent<>(
                (HoverEvent.Action<HoverEvent.Entity>) action,
                new HoverEvent.Entity(id, type, name)
            );
        }
        return Optional.of(component -> component.setHoverEvent(hoverEvent));
    };

    TagResolver KEYBIND = TagResolver.builder()
        .names("keybind", "key")
        .parameter("keybind", new StringParameter())
        .componentUpdater((component, arguments) -> component.append(KeybindComponent.of(arguments.get("keybind"))))
        .build();
    TagResolver TRANSLATION = (tagName, arguments) -> {
        if (!tagName.equals("translation") && !tagName.equals("translate")) return Optional.empty();
        String key = arguments.pollOr("translation key is not specified");
        Component[] translationArguments = new Component[arguments.size()];
        for (int i = 0; i < translationArguments.length; i++) {
            translationArguments[i] = arguments.poll(arguments.formatify()::parse);
        }
        return Optional.of(component -> component.append(TranslationComponent.of(key, translationArguments)));
    };

    TagResolver COLOR = (tagName, arguments) -> {
        String colorName = tagName.equals("color") || tagName.equals("colour") ? arguments.poll() : tagName;
        Colour color = ColorParameter.any().parse(colorName);
        return Optional.of(component -> component.setColor(color));
    };

}
