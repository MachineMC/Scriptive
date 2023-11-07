package org.machinemc.scriptive.style;

import org.machinemc.scriptive.exceptions.MalformedHexCodeException;

import java.awt.Color;
import java.util.Optional;

public class HexColor implements Colour {

    private final int rgb;

    public HexColor(int rgb) {
        this.rgb = rgb & 0x00FFFFFF;
    }

    public HexColor(int r, int g, int b) {
        this(((r & 0xFF) << 16) |
                ((g & 0xFF) << 8) |
                ((b & 0xFF)));
    }

    public HexColor(String hex) {
        this(fromHex(hex));
    }

    public HexColor(Color color) {
        this(color.getRGB());
    }

    @Override
    public boolean isDefaultColor() {
        return false;
    }

    @Override
    public int getRGB() {
        return rgb;
    }

    @Override
    public String getName() {
        return "#" + getHexString();
    }

    private static int fromHex(String hex) throws MalformedHexCodeException {
        int start = hex.startsWith("#") ? 1 : 0;
        if (hex.length() - start != 6)
            throw new MalformedHexCodeException(hex);
        try {
            return Integer.parseInt(hex.substring(start), 16);
        } catch (NumberFormatException e) {
            throw new MalformedHexCodeException(hex, e);
        }
    }

    public static boolean isValidHex(String hex) {
        int start = hex.startsWith("#") ? 1 : 0;
        if (hex.length() - start != 6)
            return false;
        for (int i = start; i < 6 + start; i++) {
            if (Character.digit(hex.charAt(i), 16) < 0)
                return false;
        }
        return true;
    }

    public static Optional<HexColor> of(String hex) {
        if (!isValidHex(hex))
            return Optional.empty();
        return Optional.of(new HexColor(hex));
    }

}
