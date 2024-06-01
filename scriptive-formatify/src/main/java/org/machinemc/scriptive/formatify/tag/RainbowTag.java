package org.machinemc.scriptive.formatify.tag;

import org.machinemc.scriptive.style.Colour;
import org.machinemc.scriptive.style.HexColor;

public class RainbowTag extends GradientTag {

    public static final Colour RED = new HexColor(0xE81416);
    public static final Colour ORANGE = new HexColor(0xFFA500);
    public static final Colour YELLOW = new HexColor(0xFAEB36);
    public static final Colour GREEN = new HexColor(0x79C314);
    public static final Colour BLUE = new HexColor(0x487DE7);
    public static final Colour INDIGO = new HexColor(0x4B369D);
    public static final Colour VIOLET = new HexColor(0x70369D);

    public RainbowTag(double offset) {
        super(new Colour[]{
                RED,
                ORANGE,
                YELLOW,
                GREEN,
                BLUE,
                INDIGO,
                VIOLET
        }, offset);
    }

}
