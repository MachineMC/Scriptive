package org.machinemc.scriptive.style;

public interface TerminalFormat {

    char CONSOLE_COLOR_CHAR = '\033';

    String getConsoleCode();

    default String getConsoleFormat() {
        return CONSOLE_COLOR_CHAR + "[" + getConsoleCode() + "m";
    }

}
