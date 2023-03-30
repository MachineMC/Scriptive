package org.machinemc.scriptive.style;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

public enum ChatStyle implements ChatCode {

    OBFUSCATED('k', 5),
    BOLD('l', 1),
    STRIKETHROUGH('m', 9),
    UNDERLINED('n', 4),
    ITALIC('o', 3);

    private final char code;
    private final int consoleCode;

    ChatStyle(char code, int consoleCode) {
        this.code = code;
        this.consoleCode = consoleCode;
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
    public String toString() {
        return new String(new char[]{167, code});
    }

    /**
     * Returns the chat style for the given character, null
     * if there is no chat style assigned to the character.
     * @param code character to get the chat style for
     * @return chat style mapped to given character
     */
    public static @Nullable ChatStyle byChar(char code) {
        for (ChatStyle value : values()) {
            if (value.code == code)
                return value;
        }
        return null;
    }

    /**
     * Returns the chat style for the given character, null
     * if there is no chat style assigned to the character.
     * @param code character to get the chat style for
     * @return chat style mapped to given character
     */
    public static @Nullable ChatStyle byChar(String code) {
        if (code.length() != 1)
            return null;
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