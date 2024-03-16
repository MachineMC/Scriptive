package org.machinemc.scriptive.components;

import org.jetbrains.annotations.Contract;
import org.machinemc.scriptive.Contents;
import org.machinemc.scriptive.events.ClickEvent;
import org.machinemc.scriptive.events.HoverEvent;
import org.machinemc.scriptive.style.*;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * A Component is an immutable object that represents how text
 * is displayed Minecraft clients. It is used in many specific contexts expecting formatted text,
 * including chat messages, written books, death messages, window titles, and the like.
 * <p>
 * The following content types are defined for vanilla client:
 * <p> text - {@link TextComponent}
 * <p> translatable - {@link TranslationComponent}
 * <p> keybind - {@link KeybindComponent}
 */
public sealed interface Component
        extends Contents, Cloneable, HoverEvent.ValueHolder<HoverEvent.Text>
        permits BaseComponent, VanillaComponent {

    /**
     * @return text format of the component
     */
    TextFormat getTextFormat();

    @Contract("null -> fail")
    void setTextFormat(TextFormat textFormat);

    /**
     * @return color of the component
     */
    Optional<Colour> getColor();

    /**
     * Changes the color of the component.
     *
     * @param color new component color
     */
    void setColor(@Nullable Colour color);

    /**
     * @return whether the component has bold style
     */
    Optional<Boolean> isBold();

    /**
     * Changes the bold style.
     *
     * @param bold new bold style
     */
    void setBold(@Nullable Boolean bold);

    /**
     * @return whether the component has obfuscated style
     */
    Optional<Boolean> isObfuscated();

    /**
     * Changes the obfuscated style.
     *
     * @param obfuscated new obfuscated style
     */
    void setObfuscated(@Nullable Boolean obfuscated);

    /**
     * @return whether the component has italic style
     */
    Optional<Boolean> isItalic();

    /**
     * Changes the italic style.
     *
     * @param italic new italic style
     */
    void setItalic(@Nullable Boolean italic);

    /**
     * @return whether the component has underlined style
     */
    Optional<Boolean> isUnderlined();

    /**
     * Changes the underlined style.
     *
     * @param underlined new underlined style
     */
    void setUnderlined(@Nullable Boolean underlined);

    /**
     * @return whether the component has strikethrough style
     */
    Optional<Boolean> isStrikethrough();

    /**
     * Changes the strikethrough style.
     *
     * @param strikethrough new strikethrough style
     */
    void setStrikethrough(@Nullable Boolean strikethrough);

    /**
     * @return the font used to display this component
     */
    Optional<String> getFont();

    /**
     * Changes the font of this component.
     *
     * @param font new font
     */
    void setFont(@Nullable String font);

    /**
     * @return text insertion used by this component
     */
    Optional<String> getInsertion();

    /**
     * Changes the insertion used by this component.
     *
     * @param insertion new insertion
     */
    void setInsertion(@Nullable String insertion);

    /**
     * @return click event used by this component
     */
    Optional<ClickEvent> getClickEvent();

    /**
     * Changes the click event used by this component.
     *
     * @param clickEvent new click event
     */
    void setClickEvent(@Nullable ClickEvent clickEvent);

    /**
     * @return hover event used by this component
     */
    Optional<HoverEvent<?>> getHoverEvent();

    /**
     * Changes the hover event used by this component.
     *
     * @param hoverEvent new hover event
     */
    void setHoverEvent(@Nullable HoverEvent<?> hoverEvent);

    /**
     * Returns the extra components of this component. (component children)
     *
     * @return component children
     */
    List<Component> getSiblings();

    /**
     * @return whether the component has children
     */
    default boolean hasSiblings() {
        return !getSiblings().isEmpty();
    }

    /**
     * Appends new string as a child text component to this component.
     *
     * @param literal text to append
     * @return this
     */
    default Component append(String literal) {
        return append(TextComponent.of(literal));
    }

    /**
     * Appends new string as a child text component to this component.
     *
     * @param literal text to append
     * @param textFormat text format to use
     * @return this
     */
    default Component append(String literal, TextFormat textFormat) {
        return append(TextComponent.of(literal, textFormat));
    }

    /**
     * Appends new child component to this component.
     *
     * @param component component to append
     * @return this
     */
    Component append(Component component);

    /**
     * Clears all children of this component.
     */
    void clearSiblings();

    /**
     * Sets text format, click event, hover event, and insertion to the same
     * values as the provided component.
     *
     * @param parent component to copy the data from
     */
    void inheritFrom(Component parent);

    void merge(Component other);

    /**
     * @return component modifier for this component
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    default ComponentModifier modify() {
        return new ComponentModifier(this);
    }

    /**
     * Returns this component and all its children separate in a single list
     * with inherited styling.
     *
     * @return list of this component and its children
     */
    List<Component> toFlatList();

    /**
     * @return legacy string component format
     */
    String toLegacyString();

    /**
     * @return raw value of this component
     */
    String getString();

    /**
     * @return clone of this component
     */
    Component clone();

    /**
     * Returns type of the component.
     * <p>
     * Is used to match component transformers.
     *
     * @return type of this component
     */
    Class<? extends BaseComponent> getType();

    @Override
    default HoverEvent.Text asHoverEventValue() {
        return new HoverEvent.Text(this);
    }

    /**
     * Component modifier is used for easier manipulation with component styling and content.
     * <p>
     * Is used for easy applying of multiple component changes at once
     * with chained method calls.
     *
     * @param <M> modifier
     * @param <C> component
     */
    class ComponentModifier<M extends ComponentModifier<M, C>, C extends Component> {

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

        public M font(@Nullable String font) {
            component.setFont(font);
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

        @SuppressWarnings("unchecked")
        protected M getThis() {
            return (M) this;
        }

        public C finish() {
            original.merge(component);
            return original;
        }

    }

}
