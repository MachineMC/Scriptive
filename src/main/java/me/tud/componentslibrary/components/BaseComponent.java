package me.tud.componentslibrary.components;

import me.tud.componentslibrary.events.ClickEvent;
import me.tud.componentslibrary.events.HoverEvent;
import me.tud.componentslibrary.color.Colour;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

    public Component color(@Nullable Colour color) {
        this.color = color;
        return this;
    }

    public Optional<Boolean> isBold() {
        return Optional.ofNullable(bold);
    }

    public Component bold(@Nullable Boolean bold) {
        this.bold = bold;
        return this;
    }

    public Optional<Boolean> isObfuscated() {
        return Optional.ofNullable(obfuscated);
    }

    public Component obfuscated(@Nullable Boolean obfuscated) {
        this.obfuscated = obfuscated;
        return this;
    }

    public Optional<Boolean> isItalic() {
        return Optional.ofNullable(italic);
    }

    public Component italic(@Nullable Boolean italic) {
        this.italic = italic;
        return this;
    }

    public Optional<Boolean> isUnderlined() {
        return Optional.ofNullable(underlined);
    }

    public Component underlined(@Nullable Boolean underlined) {
        this.underlined = underlined;
        return this;
    }

    public Optional<Boolean> isStrikethrough() {
        return Optional.ofNullable(strikethrough);
    }

    public Component strikethrough(@Nullable Boolean strikethrough) {
        this.strikethrough = strikethrough;
        return this;
    }

    public Optional<String> getFont() {
        return Optional.ofNullable(font);
    }

    public Component font(@Nullable String font) {
        this.font = font;
        return this;
    }

    @Override
    public Optional<String> getInsertion() {
        return Optional.ofNullable(insertion);
    }

    @Override
    public Component insertion(@Nullable String insertion) {
        this.insertion = insertion;
        return this;
    }

    @Override
    public Optional<ClickEvent> getClickEvent() {
        return Optional.ofNullable(clickEvent);
    }

    @Override
    public Component clickEvent(@Nullable ClickEvent clickEvent) {
        this.clickEvent = clickEvent;
        return this;
    }

    @Override
    public Optional<HoverEvent> getHoverEvent() {
        return Optional.ofNullable(hoverEvent);
    }

    @Override
    public Component hoverEvent(@Nullable HoverEvent hoverEvent) {
        this.hoverEvent = hoverEvent;
        return this;
    }

    @Override
    public @UnmodifiableView List<Component> getSiblings() {
        return Collections.unmodifiableList(siblings);
    }

    @Override
    public Component append(Component component) {
        siblings.add(component);
        return this;
    }

    @Override
    public Component clearSiblings() {
        siblings.clear();
        return this;
    }

}
