package org.machinemc.scriptive.util;

import org.machinemc.scriptive.components.Component;
import org.machinemc.scriptive.components.TextComponent;
import org.machinemc.scriptive.style.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utils for chat operations.
 */
public final class ChatUtils {

    public static final char COLOR_CHAR = 167; // §

    private static final Pattern AMP_COLOR_CODE_PATTERN = Pattern.compile("(?i)&([\\dabcdefklmnor])");
    private static final Pattern COLOR_CODE_PATTERN = Pattern.compile("(?i)" + COLOR_CHAR + "([\\dabcdefklmnor])");

    private ChatUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Translates the '&' color symbol to Minecraft's color symbol
     * @param string string to translate colors for
     * @return string with translated color codes
     */
    public static String colored(String string) {
        Objects.requireNonNull(string, "String can not be null");
        return AMP_COLOR_CODE_PATTERN.matcher(string).replaceAll(COLOR_CHAR + "$1");
    }

    /**
     * Deserializes the serialized chat component
     * @param string serialized chat component to deserialize
     * @return chat component from given string
     */
    public static TextComponent stringToComponent(String string) {
        Objects.requireNonNull(string, "String can not be null");
        Matcher matcher = COLOR_CODE_PATTERN.matcher(string);
        if (!matcher.find())
            return TextComponent.of(string);

        matcher.reset();
        List<Pair<String, String>> pairs = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        int previousStart = -1, previousEnd = -1;

        while (matcher.find()) {
            if (previousStart != -1) {
                String text = string.substring(previousEnd, matcher.start());
                builder.append(string, previousStart + 1, previousEnd);

                if (!text.isEmpty()) {
                    pairs.add(new Pair<>(builder.toString(), text));
                    builder = new StringBuilder();
                }
            }

            previousStart = matcher.start();
            previousEnd = matcher.end();
        }

        pairs.add(new Pair<>(builder.append(string, previousStart + 1, previousEnd).toString(), string.substring(previousEnd)));
        TextComponent component = TextComponent.of("");

        for (Pair<String, String> pair : pairs) {
            TextFormat format = new TextFormat();
            char[] colorCodes = pair.left().toCharArray();

            for (char code : colorCodes) {
                ChatCode chatCode = ChatCode.byChar(code).orElseThrow();
                if (chatCode.isColor() || chatCode == ChatColor.RESET) {
                    format = chatCode.asTextFormat();
                } else {
                    format.merge(chatCode.asTextFormat());
                }
            }

            TextComponent child = TextComponent.of(pair.right());
            child.setTextFormat(format);
            component.append(child);
        }

        return component;
    }

    /**
     * Serializes the given chat component as string.
     * @param component component to serialize
     * @return serialized component
     */
    public static String componentToString(Component component) {
        Objects.requireNonNull(component, "Component can not be null");
        return component.toLegacyString();
    }

    /**
     * Formats the string with vanilla Minecraft color codes with ascii
     * terminal colors.
     * @param string string to format
     * @return formatted string for console
     */
    public static String consoleFormatted(String string) {
        Objects.requireNonNull(string, "String can not be null");
        return COLOR_CODE_PATTERN.matcher(colored(string)).replaceAll(matchResult -> ChatCode.byChar(matchResult.group(1))
                .map(ChatCode::getConsoleFormat)
                .orElse(matchResult.group()));
    }

    /**
     * Formats the component with ascii terminal colors.
     * @param component component to format
     * @return formatted component as string for console
     */
    public static String consoleFormatted(Component component) {
        Objects.requireNonNull(component, "Component can not be null");
        final StringBuilder builder = new StringBuilder();
        final List<Component> components = new LinkedList<>(component.toFlatList());

        for (Component next : components) {
            final TextFormat format = next.getTextFormat();
            builder.append(ChatColor.RESET.getConsoleFormat());
            format.getColor().ifPresent((color) -> builder.append(color.getConsoleFormat()));
            for (ChatStyle style : format.getStyles(true))
                builder.append(style.getConsoleFormat());
            builder.append(next.getString());
        }

        return builder.toString();
    }

}
