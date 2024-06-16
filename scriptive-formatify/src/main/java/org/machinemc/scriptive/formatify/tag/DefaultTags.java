package org.machinemc.scriptive.formatify.tag;

import org.machinemc.scriptive.components.Component;
import org.machinemc.scriptive.components.KeybindComponent;
import org.machinemc.scriptive.components.TranslationComponent;
import org.machinemc.scriptive.events.ClickEvent;
import org.machinemc.scriptive.events.HoverEvent;
import org.machinemc.scriptive.formatify.exceptions.ParseException;
import org.machinemc.scriptive.formatify.parameter.*;
import org.machinemc.scriptive.style.Colour;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@SuppressWarnings("unchecked")
public interface DefaultTags {
    
    TagResolver COLOR = TagResolver.builder()
            .names("color", "colour")
            .parameter("color", ColorParameter.any())
            .componentUpdater((component, arguments) -> component.setColor(arguments.get("color")))
            .build();

    TagResolver DYNAMIC_COLOR = (tagName, arguments) -> {
        try {
            Colour color = ColorParameter.any().parse(tagName);
            return Optional.of(component -> component.setColor(color));
        } catch (ParseException ignore) {
            return Optional.empty();
        }
    };

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
            hoverEvent = new HoverEvent<>((HoverEvent.Action<HoverEvent.Text>) action, value);
        } else if (action == HoverEvent.SHOW_ITEM) {
            String id = arguments.pollOr("'id' is not specified");
            int amount = arguments.pollOrDefault(new IntegerParameter()::parse, 1);
            String tag = arguments.poll();
            hoverEvent = new HoverEvent<>(
                (HoverEvent.Action<HoverEvent.Item>) action,
                new HoverEvent.Item(id, amount, tag)
            );
        } else {
            UUID id = arguments.pollOr(UUID::fromString, "'id' is not specified");
            String type = arguments.poll();
            Component name = arguments.poll(arguments.formatify()::parse);
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
        for (int i = 0; i < translationArguments.length; i++)
            translationArguments[i] = arguments.poll(arguments.formatify()::parse);
        return Optional.of(component -> component.append(TranslationComponent.of(key, translationArguments)));
    };

    TagResolver GRADIENT = (tagName, arguments) -> {
        if (!tagName.equals("gradient")) return Optional.empty();
        Colour[] colors = new Colour[arguments.size()];
        double offset = 0;
        for (int i = 0; i < colors.length; i++) {
            String argument = arguments.poll();
            try {
                colors[i] = ColorParameter.any().parse(argument);
                continue;
            } catch (ParseException e) {
                if (i != colors.length - 1) throw e;
            }
            try {
                offset = new DoubleParameter().parse(argument);
            } catch (ParseException e) {
                throw new ParseException("'" + argument + "' is neither a color nor a number");
            }
            colors = Arrays.copyOf(colors, colors.length - 1);
        }
        if (colors.length < 2) throw new ParseException("A gradient must have 2 or more colors");
        return Optional.of(new GradientTag(colors, offset));
    };
    TagResolver RAINBOW = (tagName, arguments) -> {
        if (!tagName.equals("rainbow")) return Optional.empty();
        double offset = arguments.pollOrDefault(new DoubleParameter()::parse, 0d);
        return Optional.of(new RainbowTag(offset));
    };

    TagResolver ALL = TagResolver.resolvers(
            COLOR,
            BOLD,
            OBFUSCATED,
            ITALIC,
            UNDERLINED,
            STRIKETHROUGH,
            FONT,
            INSERTION,
            CLICK_EVENT,
            HOVER_EVENT,
            KEYBIND,
            TRANSLATION,
            GRADIENT,
            RAINBOW,
            DYNAMIC_COLOR
    );

}
