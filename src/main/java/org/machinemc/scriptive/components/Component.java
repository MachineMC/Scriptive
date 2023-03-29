package org.machinemc.scriptive.components;

import org.machinemc.scriptive.Contents;
import org.machinemc.scriptive.events.ClickEvent;
import org.machinemc.scriptive.events.HoverEvent;
import org.machinemc.scriptive.style.ChatStyle;
import org.machinemc.scriptive.style.Colour;
import org.machinemc.scriptive.style.TextFormat;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface Component<T> extends Contents, Cloneable {

    T getValue();

    void setValue(T t);

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

    List<Component<?>> getSiblings();

    default boolean hasSiblings() {
        return getSiblings().size() > 0;
    }

    default Component<T> append(String literal) {
        append(TextComponent.of(literal));
        return this;
    }

    Component<T> append(Component<?> component);

    void clearSiblings();

    default void merge(Component<?> other) {
        if (getClass().isInstance(other))
            //noinspection unchecked
            setValue((T) other.getValue());
        if (other.hasSiblings()) {
            clearSiblings();
            other.getSiblings().forEach(this::append);
        }
        if (other.getColor().isPresent())
            setColor(other.getColor().get());
        if (other.isBold().isPresent())
            setBold(other.isBold().get());
        if (other.isItalic().isPresent())
            setItalic(other.isItalic().get());
        if (other.isUnderlined().isPresent())
            setUnderlined(other.isUnderlined().get());
        if (other.isStrikethrough().isPresent())
            setStrikethrough(other.isStrikethrough().get());
        if (other.isObfuscated().isPresent())
            setObfuscated(other.isObfuscated().get());
        if (other.getFont().isPresent())
            setFont(other.getFont().get());
        if (other.getInsertion().isPresent())
            setInsertion(other.getInsertion().get());
        if (other.getClickEvent().isPresent())
            setClickEvent(other.getClickEvent().get());
        if (other.getHoverEvent().isPresent())
            setHoverEvent(other.getHoverEvent().get());
    }

    default TextFormat getFormat() {
        Map<ChatStyle, @Nullable Boolean> map = new HashMap<>();
        isObfuscated().ifPresent(obfuscated -> map.put(ChatStyle.OBFUSCATED, obfuscated));
        isBold().ifPresent(bold -> map.put(ChatStyle.BOLD, bold));
        isStrikethrough().ifPresent(strikethrough -> map.put(ChatStyle.STRIKETHROUGH, strikethrough));
        isUnderlined().ifPresent(underlined -> map.put(ChatStyle.UNDERLINED, underlined));
        isItalic().ifPresent(italic -> map.put(ChatStyle.ITALIC, italic));
        return new TextFormat(getColor().orElse(null), map);
    }

    default void applyFormat(TextFormat format) {
        format.getColor().ifPresent(this::setColor);
        format.getStyle(ChatStyle.BOLD).ifPresent(this::setBold);
        format.getStyle(ChatStyle.OBFUSCATED).ifPresent(this::setObfuscated);
        format.getStyle(ChatStyle.STRIKETHROUGH).ifPresent(this::setStrikethrough);
        format.getStyle(ChatStyle.UNDERLINED).ifPresent(this::setUnderlined);
        format.getStyle(ChatStyle.ITALIC).ifPresent(this::setItalic);
    }

    default ComponentModifier<T> modify() {
        return new ComponentModifier<>(this);
    }

    @Override
    default Map<String, Object> asMap() {
        Map<String, Object> map = new HashMap<>();
        getColor().ifPresent(color -> map.put("color", color.getName()));
        isBold().ifPresent(bold -> map.put("bold", bold));
        isItalic().ifPresent(italic -> map.put("italic", italic));
        isUnderlined().ifPresent(underlined -> map.put("underlined", underlined));
        isStrikethrough().ifPresent(strikethrough -> map.put("strikethrough", strikethrough));
        isObfuscated().ifPresent(obfuscated -> map.put("obfuscated", obfuscated));
        getInsertion().ifPresent(insertion -> map.put("insertion", insertion));
        getClickEvent().ifPresent(clickEvent -> map.put("clickEvent", clickEvent.asMap()));
        getHoverEvent().ifPresent(hoverEvent -> map.put("hoverEvent", hoverEvent.asMap()));
        if (hasSiblings())
            map.put("extra", getSiblings().stream().map(Contents::asMap).toArray(Map[]::new));
        return map;
    }

    String toLegacyString();

    Component<T> clone();

    class ComponentModifier<T> {

        private final Component<T> original, clone;

        private ComponentModifier(Component<T> component) {
            this.original = component;
            this.clone = original.clone();
        }

        public ComponentModifier<T> value(T t) {
            clone.setValue(t);
            return this;
        }

        public ComponentModifier<T> color(@Nullable Colour color) {
            clone.setColor(color);
            return this;
        }

        public ComponentModifier<T> bold(@Nullable Boolean bold) {
            clone.setBold(bold);
            return this;
        }

        public ComponentModifier<T> italic(@Nullable Boolean italic) {
            clone.setItalic(italic);
            return this;
        }

        public ComponentModifier<T> underlined(@Nullable Boolean underlined) {
            clone.setUnderlined(underlined);
            return this;
        }

        public ComponentModifier<T> strikethrough(@Nullable Boolean strikethrough) {
            clone.setStrikethrough(strikethrough);
            return this;
        }

        public ComponentModifier<T> obfuscated(@Nullable Boolean obfuscated) {
            clone.setObfuscated(obfuscated);
            return this;
        }

        public ComponentModifier<T> insertion(@Nullable String insertion) {
            clone.setInsertion(insertion);
            return this;
        }

        public ComponentModifier<T> clickEvent(@Nullable ClickEvent clickEvent) {
            clone.setClickEvent(clickEvent);
            return this;
        }

        public ComponentModifier<T> hoverEvent(@Nullable HoverEvent hoverEvent) {
            clone.setHoverEvent(hoverEvent);
            return this;
        }

        public ComponentModifier<T> append(String literal) {
            clone.append(literal);
            return this;
        }

        public ComponentModifier<T> append(Component<?> other) {
            clone.append(other);
            return this;
        }

        public Component<T> finish() {
            original.merge(clone);
            return original;
        }

    }

}
