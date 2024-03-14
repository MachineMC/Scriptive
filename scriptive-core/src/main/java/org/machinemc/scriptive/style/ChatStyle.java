package org.machinemc.scriptive.style;

import org.jetbrains.annotations.Range;

import java.util.Optional;

public enum ChatStyle implements ChatCode {

    OBFUSCATED("obfuscated", 'k', "5"),
    BOLD("bold", 'l', "1"),
    STRIKETHROUGH("strikethrough", 'm', "9"),
    UNDERLINED("underlined", 'n', "4"),
    ITALIC("italic", 'o', "3");

    private final String name;
    private final char code;
    private final String consoleCode;

    ChatStyle(String name, char code, String consoleCode) {
        this.name = name;
        this.code = code;
        this.consoleCode = consoleCode;
    }

    public String getName() {
        return name;
    }

    @Override
    public char getCode() {
        return code;
    }

    @Override
    public String getConsoleCode() {
        return consoleCode;
    }

    @Override
    public int getIntCode() {
        return ordinal();
    }

    @Override
    public boolean isColor() {
        return false;
    }

    @Override
    public boolean isFormat() {
        return true;
    }

    @Override
    public TextFormat asTextFormat() {
        return new TextFormat(this);
    }

    @Override
    public String toString() {
        return new String(new char[]{167, code});
    }

    /**
     * Returns the chat style for the given character, null
     * if there is no chat style assigned to the character.
     * @param code character to get the chat style for
     * @return chat style mapped to given character
     */
    public static Optional<ChatStyle> byChar(char code) {
        for (ChatStyle value : values()) {
            if (value.code == code)
                return Optional.of(value);
        }
        return Optional.empty();
    }

    /**
     * Returns the chat style for the given character, null
     * if there is no chat style assigned to the character.
     * @param code character to get the chat style for
     * @return chat style mapped to given character
     */
    public static Optional<ChatStyle> byChar(String code) {
        if (code.length() != 1)
            return Optional.empty();
        return byChar(code.charAt(0));
    }

    /**
     * Returns the chat style by its numeric code.
     * @param code numeric code of the chat style
     * @return chat style with given numeric code
     */
    public static ChatStyle byCode(@Range(from = 0, to = 21) int code) {
        if (code >= values().length)
            throw new IllegalArgumentException("Unsupported ChatStyle");
        return values()[code];
    }

}
