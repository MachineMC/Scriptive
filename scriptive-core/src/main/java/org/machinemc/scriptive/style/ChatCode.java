package org.machinemc.scriptive.style;

import org.jetbrains.annotations.Range;

import java.util.Optional;

/**
 * Represents a chat format that can be represented as a
 * code.
 */
public sealed interface ChatCode extends TerminalFormat permits ChatColor, ChatStyle {

    /**
     * @return character code of the chat code
     */
    char getCode();

    /**
     * @return console code of the chat code
     */
    String getConsoleCode();

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
     *
     * @param code character to get the chat code for
     * @return chat code mapped to given character
     */
    static Optional<ChatCode> byChar(char code) {
        return ChatColor.byChar(code).map(chatColor -> (ChatCode) chatColor).or(() -> ChatStyle.byChar(code));
    }

    /**
     * Returns the chat code for the given character, null
     * if there is no chat code assigned to the character.
     *
     * @param code character to get the chat code for
     * @return chat code mapped to given character
     */
    static Optional<ChatCode> byChar(String code) {
        if (code.length() != 1)
            return Optional.empty();
        return byChar(code.charAt(0));
    }

    /**
     * Returns the chat code by its numeric code.
     *
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
