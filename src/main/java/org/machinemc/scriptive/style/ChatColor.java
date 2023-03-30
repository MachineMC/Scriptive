package org.machinemc.scriptive.style;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.Locale;

/**
 * Default color values for Minecraft chat.
 */
public enum ChatColor implements ChatCode, Colour {

    BLACK('0', 30, 0x000000),
    DARK_BLUE('1', 34, 0x0000AA),
    DARK_GREEN('2', 32, 0x00AA00),
    DARK_AQUA('3', 36, 0x00AAAA),
    DARK_RED('4', 31, 0xAA0000),
    DARK_PURPLE('5', 35, 0xAA00AA),
    GOLD('6', 33, 0xFFAA00),
    GRAY('7', 37, 0xAAAAAA),
    DARK_GRAY('8', 90, 0x555555),
    BLUE('9', 94, 0x5555FF),
    GREEN('a', 92, 0x55FF55),
    AQUA('b', 96, 0x55FFFF),
    RED('c', 91, 0xFF5555),
    LIGHT_PURPLE('d', 95, 0xFF55FF),
    YELLOW('e', 93, 0xFFFF55),
    WHITE('f', 97, 0xFFFFFF),
    RESET('r', 0, -1);

    private final char code;
    private final int consoleCode;
    private final int hexCode;
    private final boolean isColor;

    ChatColor(char code, int consoleCode, int hexCode) {
        this.code = code;
        this.consoleCode = consoleCode;
        this.hexCode = hexCode;
        this.isColor = code != 'r';
    }

    @Override
    public char getCode() {
        return code;
    }

    @Override
    public int getConsoleCode() {
        return consoleCode;
    }

    @Override
    public @Range(from = 0, to = 21) int getIntCode() {
        return ordinal();
    }

    @Override
    public boolean isColor() {
        return isColor;
    }

    @Override
    public boolean isFormat() {
        return false;
    }

    @Override
    public boolean isDefaultColor() {
        return true;
    }

    @Override
    public int getRGB() {
        return hexCode;
    }

    @Override
    public String getName() {
        return name().toLowerCase(Locale.ENGLISH);
    }

    @Override
    public String toString() {
        return new String(new char[]{167, code});
    }

    /**
     * Returns the chat color for the given character, null
     * if there is no chat color assigned to the character.
     * @param code character to get the chat color for
     * @return chat color mapped to given character
     */
    public static @Nullable ChatColor byChar(char code) {
        for (ChatColor value : values()) {
            if (value.code == code)
                return value;
        }
        return null;
    }

    /**
     * Returns the chat color for the given character, null
     * if there is no chat color assigned to the character.
     * @param code character to get the chat color for
     * @return chat color mapped to given character
     */
    public static @Nullable ChatColor byChar(String code) {
        if (code.length() != 1)
            return null;
        return byChar(code.charAt(0));
    }

    /**
     * Returns the chat color by its numeric code.
     * @param code numeric code of the chat color
     * @return chat color with given numeric code
     */
    public static ChatColor byCode(@Range(from = 0, to = 21) int code) {
        if (code >= values().length)
            throw new IllegalArgumentException("Unsupported ChatColor");
        return values()[code];
    }

}