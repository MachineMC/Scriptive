package me.tud.componentslibrary.util;

import me.tud.componentslibrary.components.Component;
import me.tud.componentslibrary.style.ChatCode;
import me.tud.componentslibrary.style.ChatColor;
import me.tud.componentslibrary.style.ChatStyle;
import me.tud.componentslibrary.style.Colour;

import java.util.regex.Pattern;

/**
 * Utils for chat operations.
 */
public final class ChatUtils {

//    private static final LegacyComponentSerializer legacyComponentSerializer = LegacyComponentSerializer.builder().build();
    public static final char COLOR_CHAR = 167; // §
    private static final char CONSOLE_COLOR_CHAR = '\033';

    private static final Pattern AMP_COLOR_CODE_PATTERN = Pattern.compile("&([\\daAbBcCdDeEfFkKlLmMnNoOrR])");
    private static final Pattern COLOR_CODE_PATTERN = Pattern.compile(COLOR_CHAR + "([\\daAbBcCdDeEfFkKlLmMnNoOrR])");

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

//    /**
//     * Deserializes the serialized chat component
//     * @param string serialized chat component to deserialize
//     * @return chat component from given string
//     */
//    public static TextComponent stringToComponent(String string) {
//        return legacyComponentSerializer.deserialize(string);
//    }

    /**
     * Serializes the given chat component as string.
     * @param component component to serialize
     * @return serialized component
     */
    public static String componentToString(Component<? extends String> component) {
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
            ChatCode chatCode = ChatColor.byChar(matchResult.group(1));
            if (chatCode == null)
                chatCode = ChatStyle.byChar(matchResult.group(1));
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
    public static String consoleFormatted(Component<?> component) {
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
