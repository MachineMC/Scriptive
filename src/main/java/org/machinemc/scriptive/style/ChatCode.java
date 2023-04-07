package org.machinemc.scriptive.style;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

public interface ChatCode {

    /**
     * @return character code of the chat code
     */
    char getCode();

    /**
     * @return console code of the chat code
     */
    int getConsoleCode();

    /**
     * @return numeric code of the chat code
     */
    int getIntCode();

    /**
     * @return whether the ChatCode is a color
     */
    boolean isColor();

    /**
     * @return whether the ChatCode is a format
     */
    boolean isFormat();

    TextFormat asTextFormat();


    /**
     * Returns the chat code for the given character, null
     * if there is no chat code assigned to the character.
     * @param code character to get the chat code for
     * @return chat code mapped to given character
     */
    static @Nullable ChatCode byChar(char code) {
        ChatCode chatCode = ChatColor.byChar(code);
        if (chatCode == null)
            return ChatStyle.byChar(code);
        return chatCode;
    }

    /**
     * Returns the chat code for the given character, null
     * if there is no chat code assigned to the character.
     * @param code character to get the chat code for
     * @return chat code mapped to given character
     */
    static @Nullable ChatCode byChar(String code) {
        if (code.length() != 1)
            return null;
        return byChar(code.charAt(0));
    }

    /**
     * Returns the chat code by its numeric code.
     * @param code numeric code of the chat code
     * @return chat code with given numeric code
     */
    static ChatCode byCode(@Range(from = 0, to = 21) int code) {
        if (code >= ChatColor.values().length + ChatStyle.values().length)
            throw new IllegalArgumentException("Unsupported ChatColor");
        if (code <= 16)
            return ChatColor.byCode(code);
        return ChatStyle.byCode(code);
    }

}
