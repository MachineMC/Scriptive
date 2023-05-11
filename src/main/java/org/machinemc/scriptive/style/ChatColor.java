package org.machinemc.scriptive.style;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.Locale;

/**
 * Default color values for Minecraft chat.
 */
public enum ChatColor implements ChatCode, Colour {

    BLACK('0', 0x000000),
    DARK_BLUE('1', 0x0000AA),
    DARK_GREEN('2', 0x00AA00),
    DARK_AQUA('3', 0x00AAAA),
    DARK_RED('4', 0xAA0000),
    DARK_PURPLE('5', 0xAA00AA),
    GOLD('6', 0xFFAA00),
    GRAY('7', 0xAAAAAA),
    DARK_GRAY('8', 0x555555),
    BLUE('9', 0x5555FF),
    GREEN('a', 0x55FF55),
    AQUA('b', 0x55FFFF),
    RED('c', 0xFF5555),
    LIGHT_PURPLE('d', 0xFF55FF),
    YELLOW('e', 0xFFFF55),
    WHITE('f', 0xFFFFFF),
    RESET('r', "0", -1);

    private final char code;
    private final @Nullable String consoleCode;
    private final int hexCode;
    private final boolean isColor;

    ChatColor(char code, int hexCode) {
        this(code, null, hexCode);
    }

    ChatColor(char code, @Nullable String consoleCode, int hexCode) {
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
    public String getConsoleCode() {
        return consoleCode != null ? consoleCode : Colour.super.getConsoleCode();
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
    public TextFormat asTextFormat() {
        return new TextFormat(this);
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
    public static ChatColor byCode(@Range(from = 0, to = 16) int code) {
        if (code >= values().length)
            throw new IllegalArgumentException("Unsupported ChatColor");
        return values()[code];
    }

}
