package org.machinemc.scriptive.style;

import java.awt.Color;
import java.util.Locale;

public interface Colour extends TerminalFormat {

    /**
     * Returns whether the color is a {@link ChatColor} or another implementation
     * @return true if it's a {@link ChatColor}, else false
     */
    boolean isDefaultColor();

    /**
     * @return rgb value of the color
     */
    int getRGB();

    /**
     * Returns the string identifier used to get the color in the component's JSON.<br></br>
     * For example, <code>ChatColor.RED</code> would return 'red' and <code>new HexColor("#123ABC") would return '#123ABC'</code>
     * @return name
     */
    String getName();

    /**
     * Returns this color as an awt color representation.
     * @return awt color
     */
    default Color asColor() {
        return new Color(getRGB());
    }

    default TextFormat asTextFormat() {
        return new TextFormat(this);
    }

    /**
     * Returns the red value of this color.
     * @return red value
     */
    default int getRed() {
        return (getRGB() >> 16) & 0xFF;
    }

    /**
     * Returns the green value of this color.
     * @return green value
     */
    default int getGreen() {
        return (getRGB() >> 8) & 0xFF;
    }

    /**
     * Returns the blue value of this color.
     * @return blue value
     */
    default int getBlue() {
        return getRGB() & 0xFF;
    }

    /**
     * Returns the hex string of this color.
     * @return hex string
     */
    default String getHexString() {
        return "%02x%02x%02x".formatted(getRed(), getGreen(), getBlue()).toUpperCase(Locale.ENGLISH);
    }

    @Override
    default String getConsoleCode() {
        return "38;2;" + getRed() + ";" + getGreen() + ";" + getBlue();
    }

}
