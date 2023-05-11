package org.machinemc.scriptive.components;

import org.machinemc.scriptive.Contents;
import org.machinemc.scriptive.events.ClickEvent;
import org.machinemc.scriptive.events.HoverEvent;
import org.machinemc.scriptive.style.*;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public interface Component extends Contents, Cloneable {

    Optional<Colour> getColor();

    void setColor(@Nullable Colour color);

    Optional<Boolean> isBold();

    void setBold(@Nullable Boolean bold);

    Optional<Boolean> isObfuscated();

    void setObfuscated(@Nullable Boolean obfuscated);

    Optional<Boolean> isItalic();

    void setItalic(@Nullable Boolean italic);

    Optional<Boolean> isUnderlined();

    void setUnderlined(@Nullable Boolean underlined);

    Optional<Boolean> isStrikethrough();

    void setStrikethrough(@Nullable Boolean strikethrough);

    Optional<String> getFont();

    void setFont(@Nullable String font);

    Optional<String> getInsertion();

    void setInsertion(@Nullable String insertion);

    Optional<ClickEvent> getClickEvent();

    void setClickEvent(@Nullable ClickEvent clickEvent);

    Optional<HoverEvent> getHoverEvent();

    void setHoverEvent(@Nullable HoverEvent hoverEvent);

    List<Component> getSiblings();

    default boolean hasSiblings() {
        return getSiblings().size() > 0;
    }

    default Component append(String literal) {
        append(TextComponent.of(literal));
        return this;
    }

    Component append(Component component);

    void clearSiblings();

    void merge(Component other);

    TextFormat getFormat();

    void applyFormat(TextFormat format);

    default ComponentModifier<?> modify() {
        return new ComponentModifier<>(this);
    }

    List<Component> separatedComponents();

    String toLegacyString();

    String flatten();

    Component clone();

    class ComponentModifier<C extends Component> {

        protected final C original, clone;

        @SuppressWarnings("unchecked")
        protected ComponentModifier(C component) {
            this.original = component;
            this.clone = (C) original.clone();
        }

        public ComponentModifier<C> color(@Nullable Colour color) {
            clone.setColor(color);
            return this;
        }

        public ComponentModifier<C> bold(@Nullable Boolean bold) {
            clone.setBold(bold);
            return this;
        }

        public ComponentModifier<C> italic(@Nullable Boolean italic) {
            clone.setItalic(italic);
            return this;
        }

        public ComponentModifier<C> underlined(@Nullable Boolean underlined) {
            clone.setUnderlined(underlined);
            return this;
        }

        public ComponentModifier<C> strikethrough(@Nullable Boolean strikethrough) {
            clone.setStrikethrough(strikethrough);
            return this;
        }

        public ComponentModifier<C> obfuscated(@Nullable Boolean obfuscated) {
            clone.setObfuscated(obfuscated);
            return this;
        }

        public ComponentModifier<C> insertion(@Nullable String insertion) {
            clone.setInsertion(insertion);
            return this;
        }

        public ComponentModifier<C> clickEvent(@Nullable ClickEvent clickEvent) {
            clone.setClickEvent(clickEvent);
            return this;
        }

        public ComponentModifier<C> hoverEvent(@Nullable HoverEvent hoverEvent) {
            clone.setHoverEvent(hoverEvent);
            return this;
        }

        public ComponentModifier<C> append(String literal) {
            clone.append(literal);
            return this;
        }

        public ComponentModifier<C> append(Component other) {
            clone.append(other);
            return this;
        }

        public C finish() {
            original.merge(clone);
            return original;
        }

    }

}
