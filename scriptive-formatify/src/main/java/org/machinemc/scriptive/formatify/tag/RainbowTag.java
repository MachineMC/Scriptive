package org.machinemc.scriptive.formatify.tag;

import org.machinemc.scriptive.style.Colour;
import org.machinemc.scriptive.style.HexColor;

public class RainbowTag extends GradientTag {

    public static final Colour RED = new HexColor("e81416");
    public static final Colour ORANGE = new HexColor("ffa500");
    public static final Colour YELLOW = new HexColor("faeb36");
    public static final Colour GREEN = new HexColor("79c314");
    public static final Colour BLUE = new HexColor("487de7");
    public static final Colour INDIGO = new HexColor("4b369d");
    public static final Colour VIOLET = new HexColor("70369d");

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
