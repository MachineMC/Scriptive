package org.machinemc.scriptive.components;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import org.machinemc.scriptive.events.ClickEvent;
import org.machinemc.scriptive.events.HoverEvent;
import org.machinemc.scriptive.style.ChatStyle;
import org.machinemc.scriptive.style.Colour;
import org.machinemc.scriptive.style.TextFormat;

import java.util.*;

public abstract class BaseComponent implements Component {

    private final List<Component> siblings;
    private @Nullable Colour color;
    private @Nullable Boolean bold, italic, underlined, strikethrough, obfuscated;
    private @Nullable String font, insertion;
    private @Nullable ClickEvent clickEvent;
    private @Nullable HoverEvent hoverEvent;

    protected BaseComponent() {
        this(new ArrayList<>());
    }

    protected BaseComponent(List<Component> siblings) {
        this.siblings = siblings;
    }

    public Optional<Colour> getColor() {
        return Optional.ofNullable(color);
    }

    public void setColor(@Nullable Colour color) {
        this.color = color;
    }

    public Optional<Boolean> isBold() {
        return Optional.ofNullable(bold);
    }

    public void setBold(@Nullable Boolean bold) {
        this.bold = bold;
    }

    public Optional<Boolean> isObfuscated() {
        return Optional.ofNullable(obfuscated);
    }

    public void setObfuscated(@Nullable Boolean obfuscated) {
        this.obfuscated = obfuscated;
    }

    public Optional<Boolean> isItalic() {
        return Optional.ofNullable(italic);
    }

    public void setItalic(@Nullable Boolean italic) {
        this.italic = italic;
    }

    public Optional<Boolean> isUnderlined() {
        return Optional.ofNullable(underlined);
    }

    public void setUnderlined(@Nullable Boolean underlined) {
        this.underlined = underlined;
    }

    public Optional<Boolean> isStrikethrough() {
        return Optional.ofNullable(strikethrough);
    }

    public void setStrikethrough(@Nullable Boolean strikethrough) {
        this.strikethrough = strikethrough;
    }

    public Optional<String> getFont() {
        return Optional.ofNullable(font);
    }

    public void setFont(@Nullable String font) {
        this.font = font;
    }

    @Override
    public Optional<String> getInsertion() {
        return Optional.ofNullable(insertion);
    }

    @Override
    public void setInsertion(@Nullable String insertion) {
        this.insertion = insertion;
    }

    @Override
    public Optional<ClickEvent> getClickEvent() {
        return Optional.ofNullable(clickEvent);
    }

    @Override
    public void setClickEvent(@Nullable ClickEvent clickEvent) {
        this.clickEvent = clickEvent;
    }

    @Override
    public Optional<HoverEvent> getHoverEvent() {
        return Optional.ofNullable(hoverEvent);
    }

    @Override
    public void setHoverEvent(@Nullable HoverEvent hoverEvent) {
        this.hoverEvent = hoverEvent;
    }

    @Override
    public @UnmodifiableView List<Component> getSiblings() {
        return Collections.unmodifiableList(siblings);
    }

    @Override
    public Component append(Component component) {
        siblings.add(component.clone());
        return this;
    }

    @Override
    public void clearSiblings() {
        siblings.clear();
    }

    @Override
    public String toLegacyString() {
        List<Component> components = separateComponent(this);
        StringBuilder builder = new StringBuilder();
        for (Component component : components)
            builder.append(toLegacyString(component));
        return builder.toString();
    }

    @Override
    public abstract BaseComponent clone();

    private static List<Component> separateComponent(Component component) {
        List<Component> components = new LinkedList<>();
        Component parent = component.clone();
        components.add(parent);
        for (Component child : parent.getSiblings()) {
            child.getColor().ifPresentOrElse(k -> {}, () -> child.setColor(parent.getColor().orElse(null)));
            child.isBold().ifPresentOrElse(k -> {}, () -> child.setBold(parent.isBold().orElse(null)));
            child.isItalic().ifPresentOrElse(k -> {}, () -> child.setItalic(parent.isItalic().orElse(null)));
            child.isUnderlined().ifPresentOrElse(k -> {}, () -> child.setUnderlined(parent.isUnderlined().orElse(null)));
            child.isStrikethrough().ifPresentOrElse(k -> {}, () -> child.setStrikethrough(parent.isStrikethrough().orElse(null)));
            child.isObfuscated().ifPresentOrElse(k -> {}, () -> child.setObfuscated(parent.isObfuscated().orElse(null)));
            child.getFont().ifPresentOrElse(k -> {}, () -> child.setFont(parent.getFont().orElse(null)));
            child.getInsertion().ifPresentOrElse(k -> {}, () -> child.setInsertion(parent.getInsertion().orElse(null)));
            child.getClickEvent().ifPresentOrElse(k -> {}, () -> child.setClickEvent(parent.getClickEvent().orElse(null)));
            child.getHoverEvent().ifPresentOrElse(k -> {}, () -> child.setHoverEvent(parent.getHoverEvent().orElse(null)));
            components.addAll(separateComponent(child));
        }
        parent.clearSiblings();
        return components;
    }

    private static String toLegacyString(Component component) {
        StringBuilder builder = new StringBuilder();
        TextFormat format = component.getFormat();
        format.getColor().ifPresent(color -> {
            if (color.isDefaultColor()) {
                builder.append(color);
            } else {
                builder.append("&x&").append(String.join("&", color.getHexString().split("")));
            }
        });
        for (ChatStyle style : format.getStyles(true))
            builder.append(style);
        builder.append(component.flatten());
        return builder.toString();
    }

}
