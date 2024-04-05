package org.machinemc.scriptive.style;

/**
 * Represents a format that is applicable to terminal interface.
 */
public interface TerminalFormat {

    /**
     * Character for terminal formatting.
     */
    char CONSOLE_COLOR_CHAR = '\033';

    /**
     * Returns code used by terminal for this format.
     *
     * @return console code
     */
    String getConsoleCode();

    /**
     * Returns string that represents this format in the terminal interface.
     *
     * @return console format
     */
    default String getConsoleFormat() {
        return CONSOLE_COLOR_CHAR + "[" + getConsoleCode() + "m";
    }

}
