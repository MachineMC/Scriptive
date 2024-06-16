package org.machinemc.scriptive.formatify.tag;

import org.machinemc.scriptive.components.Component;
import org.machinemc.scriptive.components.TextComponent;
import org.machinemc.scriptive.style.Colour;
import org.machinemc.scriptive.style.HexColor;

import java.util.List;

public class GradientTag implements Tag {

    private final Colour[] colors;
    private final double offset;
    private final double colorLength;

    public GradientTag(Colour[] colors, double offset) {
        this.colors = colors;
        this.offset = offset;
        this.colorLength = (double) 1 / (colors.length - 1);
    }

    protected Colour lerp(double value) {
        double valueRatio = value / colorLength;
        int index = (int) Math.floor(valueRatio);
        Colour first = colors[index % colors.length];
        Colour second = colors[(index + 1) % colors.length];
        double colorFraction = valueRatio % 1;
        return new HexColor(
                (int) (first.getRed() + (second.getRed() - first.getRed()) * colorFraction),
                (int) (first.getGreen() + (second.getGreen() - first.getGreen()) * colorFraction),
                (int) (first.getBlue() + (second.getBlue() - first.getBlue()) * colorFraction)
        );
    }

    @Override
    public void apply(Component component) {
        int length = length(component);
        double value = offset, step = (double) 1 / length;
        TextComponent gradient = TextComponent.empty();
        List<Component> parts = component.toFlatList();
        for (Component part : parts) {
            if (!(part instanceof TextComponent)) {
                value += step;
                if (part.getColor().isEmpty())
                    part.setColor(lerp(value));
                gradient.append(part);
            }
            String string = part.getString();
            if (string.isEmpty())
                continue;
            for (int i = 0, strLen = string.length(); i < strLen; i++) {
                value += step;
                Colour color = part.getColor().orElse(lerp(value));
                gradient.append(string.charAt(i) + "", color.asTextFormat());
            }
        }
        component.clearSiblings();
        component.merge(gradient);
    }

    private static int length(Component component) {
        int length = 0;
        length += component instanceof TextComponent ? component.getString().length() : 1;
        for (Component sibling : component.getSiblings())
            length += length(sibling);
        return length;
    }

}
