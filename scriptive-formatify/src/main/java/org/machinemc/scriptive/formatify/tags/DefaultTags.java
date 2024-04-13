package org.machinemc.scriptive.formatify.tags;

import org.machinemc.scriptive.events.ClickEvent;
import org.machinemc.scriptive.formatify.Result;
import org.machinemc.scriptive.formatify.arguments.ColorArgument;

import java.util.List;

public final class DefaultTags {

    public static final Tag COLOR = Tag.builder("color", "colour")
            .argument("color", new ColorArgument())
            .update((component, arguments) -> component.setColor(arguments.getArgument("color")))
            .build();

    public static final Tag OBFUSCATED = Tag.noArg((component, arguments) -> component.setObfuscated(true),
            "obfuscated", "obf", "magic");

    public static final Tag BOLD = Tag.noArg((component, arguments) -> component.setBold(true), "bold", "b");

    public static final Tag STRIKETHROUGH = Tag.noArg((component, arguments) -> component.setStrikethrough(true),
            "strikethrough", "strike");

    public static final Tag UNDERLINED = Tag.noArg((component, arguments) -> component.setUnderlined(true),
            "underlined", "underline", "u");

    public static final Tag ITALIC = Tag.noArg((component, arguments) -> component.setItalic(true), "italic", "italics", "i");

    public static final Tag CLICK_EVENT = Tag.builder("click", "click_event")
            .argument("action", unparsed -> {
                try {
                    return Result.of(ClickEvent.Action.valueOf(unparsed.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    return Result.error('\'' + unparsed + "' is not a valid click action");
                }
            })
            .argument("value", Result::of)
            .update((component, arguments) -> {
                ClickEvent.Action action = arguments.getArgument("action");
                String value = arguments.getArgument("value");
                component.setClickEvent(new ClickEvent(action, value));
            })
            .build();

    public static final Tag RESET = Tag.noArg((component, arguments) -> {}, "reset");

    public static final Tag NEW_LINE = Tag.noArg((component, arguments) -> component.setText("\n"), "newline", "nl");

    public static final List<Tag> ALL = List.of(
            COLOR,
            OBFUSCATED,
            BOLD,
            STRIKETHROUGH,
            UNDERLINED,
            ITALIC,
            CLICK_EVENT,
            RESET,
            NEW_LINE
    );

}
