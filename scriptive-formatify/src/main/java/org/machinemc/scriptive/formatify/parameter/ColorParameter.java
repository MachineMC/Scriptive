package org.machinemc.scriptive.formatify.parameter;

import org.machinemc.scriptive.formatify.exceptions.ParseException;
import org.machinemc.scriptive.style.ChatColor;
import org.machinemc.scriptive.style.Colour;
import org.machinemc.scriptive.style.HexColor;

public class ColorParameter implements Parameter<Colour> {

    private static final ColorParameter
        ANY = new ColorParameter(true, true),
        NAMED = new ColorParameter(true, false),
        HEX = new ColorParameter(false, true);

    private final boolean allowNamedColors, allowHexColors;

    private ColorParameter(boolean allowNamedColors, boolean allowHexColors) {
        this.allowNamedColors = allowNamedColors;
        this.allowHexColors = allowHexColors;
    }

    @Override
    public Colour parse(String string) throws ParseException {
        Colour color = null;
        if (allowNamedColors)
            color = ChatColor.byName(string).orElse(null);
        if (color == null && allowHexColors)
            color = HexColor.of(string).orElse(null);
        if (color == null)
            throw new ParseException("Couldn't parse '" + string + "' as a color");
        return color;
    }

    public static ColorParameter any() {
        return ANY;
    }

    public static ColorParameter named() {
        return NAMED;
    }

    public static ColorParameter hex() {
        return HEX;
    }

}
