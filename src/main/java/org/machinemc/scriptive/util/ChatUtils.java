package org.machinemc.scriptive.util;

import org.machinemc.scriptive.components.Component;
import org.machinemc.scriptive.components.TextComponent;
import org.machinemc.scriptive.style.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utils for chat operations.
 */
public final class ChatUtils {

    public static final char COLOR_CHAR = 167; // §
    private static final char CONSOLE_COLOR_CHAR = '\033';

    private static final Pattern AMP_COLOR_CODE_PATTERN = Pattern.compile("(?i)&([\\dabcdefklmnor])");
    private static final Pattern COLOR_CODE_PATTERN = Pattern.compile("(?i)" + COLOR_CHAR + "([\\dabcdefklmnor])");

    public static final String DEFAULT_CHAT_FORMAT = "<%name%> %message%";

    private ChatUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Translates the '&' color symbol to Minecraft's color symbol
     * @param string string to translate colors for
     * @return string with translated color codes
     */
    public static String colored(String string) {
        return AMP_COLOR_CODE_PATTERN.matcher(string).replaceAll(COLOR_CHAR + "$1");
    }

    /**
     * Deserializes the serialized chat component
     * @param string serialized chat component to deserialize
     * @return chat component from given string
     */
    public static TextComponent stringToComponent(String string) {
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
                ChatCode chatCode = ChatCode.byChar(code);
                assert chatCode != null;
                if (chatCode.isColor() || chatCode == ChatColor.RESET) {
                    format = chatCode.asTextFormat();
                } else {
                    format.merge(chatCode.asTextFormat());
                }
            }

            TextComponent child = TextComponent.of(pair.right());
            child.applyFormat(format);
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
        return component.toLegacyString();
    }

    /**
     * Formats the string with vanilla Minecraft color codes with ascii
     * terminal colors.
     * @param string string to format
     * @return formatted string for console
     */
    public static String consoleFormatted(String string) {
        return COLOR_CODE_PATTERN.matcher(colored(string)).replaceAll(matchResult -> {
            ChatCode chatCode = ChatCode.byChar(matchResult.group(1));
            if (chatCode == null || chatCode.getConsoleCode() < 0)
                return matchResult.group();
            return CONSOLE_COLOR_CHAR + "[" + (chatCode.isColor() ? "0;" : "") + chatCode.getConsoleCode() + "m";
        });
    }

    /**
     * Formats the component with ascii terminal colors.
     * @param component component to format
     * @return formatted component as string for console
     */
    public static String consoleFormatted(Component component) {
        return consoleFormatted(component.toLegacyString());
    }

    /**
     * Converts text color to a ascii string for terminal.
     * @param color color to convert
     * @return color converted to ascii color format
     */
    public static String asciiColor(Colour color) {
        return "\u001B[38;2;" + color.getRed() + ";" + color.getGreen() + ";" + color.getBlue() + "m";
    }

}
