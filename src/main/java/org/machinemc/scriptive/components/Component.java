package org.machinemc.scriptive.components;

import org.jetbrains.annotations.Contract;
import org.machinemc.scriptive.Contents;
import org.machinemc.scriptive.events.ClickEvent;
import org.machinemc.scriptive.events.HoverEvent;
import org.machinemc.scriptive.style.*;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public interface Component extends Contents, Cloneable, HoverEvent.ValueHolder<HoverEvent.Text> {

    TextFormat getTextFormat();

    @Contract("null -> fail")
    void setTextFormat(TextFormat textFormat);

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

    Optional<HoverEvent<?>> getHoverEvent();

    void setHoverEvent(@Nullable HoverEvent<?> hoverEvent);

    List<Component> getSiblings();

    default boolean hasSiblings() {
        return !getSiblings().isEmpty();
    }

    default Component append(String literal) {
        return append(TextComponent.of(literal));
    }

    default Component append(String literal, TextFormat textFormat) {
        return append(TextComponent.of(literal, textFormat));
    }

    Component append(Component component);

    void clearSiblings();

    void inheritFrom(Component parent);

    void merge(Component other);

    @SuppressWarnings({"unchecked", "rawtypes"})
    default ComponentModifier modify() {
        return new ComponentModifier(this) {
            @Override
            protected ComponentModifier getThis() {
                return this;
            }
        };
    }

    List<Component> toFlatList();

    String toLegacyString();

    String getString();

    Component clone();

    @Override
    default HoverEvent.Text asHoverEventValue() {
        return new HoverEvent.Text(this);
    }

    abstract class ComponentModifier<M extends ComponentModifier<M, C>, C extends Component> {

        protected final C component;
        private final C original;

        @SuppressWarnings("unchecked")
        protected ComponentModifier(C component) {
            this.original = component;
            this.component = (C) original.clone();
        }

        public M color(@Nullable Colour color) {
            component.setColor(color);
            return getThis();
        }

        public M bold(@Nullable Boolean bold) {
            component.setBold(bold);
            return getThis();
        }

        public M italic(@Nullable Boolean italic) {
            component.setItalic(italic);
            return getThis();
        }

        public M underlined(@Nullable Boolean underlined) {
            component.setUnderlined(underlined);
            return getThis();
        }

        public M strikethrough(@Nullable Boolean strikethrough) {
            component.setStrikethrough(strikethrough);
            return getThis();
        }

        public M obfuscated(@Nullable Boolean obfuscated) {
            component.setObfuscated(obfuscated);
            return getThis();
        }

        public M insertion(@Nullable String insertion) {
            component.setInsertion(insertion);
            return getThis();
        }

        public M clickEvent(@Nullable ClickEvent clickEvent) {
            component.setClickEvent(clickEvent);
            return getThis();
        }

        public M hoverEvent(@Nullable HoverEvent<?> hoverEvent) {
            component.setHoverEvent(hoverEvent);
            return getThis();
        }

        public M append(String literal) {
            component.append(literal);
            return getThis();
        }

        public M append(Component other) {
            component.append(other);
            return getThis();
        }

        protected abstract M getThis();

        public C finish() {
            original.merge(component);
            return original;
        }

    }

}
