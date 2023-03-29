package org.machinemc.scriptive.style;

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

}
