package me.tud.componentslibrary.color;

import java.awt.Color;
import java.util.Locale;

public interface Colour {

    boolean isDefaultColor();

    boolean isColor();

    boolean isFormat();

    int getRGB();

    String getName();

    default Color asColor() {
        return new Color(getRGB());
    }

    default int getRed() {
        return (getRGB() >> 16) & 0xFF;
    }

    default int getGreen() {
        return (getRGB() >> 8) & 0xFF;
    }

    default int getBlue() {
        return getRGB() & 0xFF;
    }

    default String getHexString() {
        return "%02x%02x%02x".formatted(getRed(), getGreen(), getBlue()).toUpperCase(Locale.ENGLISH);
    }

}
