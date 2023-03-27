package me.tud.componentslibrary.components;

import me.tud.componentslibrary.events.ClickEvent;
import me.tud.componentslibrary.Contents;
import me.tud.componentslibrary.events.HoverEvent;
import me.tud.componentslibrary.color.Colour;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface Component extends Contents {

    Optional<Colour> getColor();

    Component color(@Nullable Colour color);

    Optional<Boolean> isBold();

    Component bold(@Nullable Boolean bold);

    Optional<Boolean> isObfuscated();

    Component obfuscated(@Nullable Boolean obfuscated);

    Optional<Boolean> isItalic();

    Component italic(@Nullable Boolean italic);

    Optional<Boolean> isUnderlined();

    Component underlined(@Nullable Boolean underlined);

    Optional<Boolean> isStrikethrough();

    Component strikethrough(@Nullable Boolean strikethrough);

    Optional<String> getFont();

    Component font(@Nullable String font);

    Optional<String> getInsertion();

    Component insertion(@Nullable String insertion);

    Optional<ClickEvent> getClickEvent();

    Component clickEvent(@Nullable ClickEvent clickEvent);

    Optional<HoverEvent> getHoverEvent();

    Component hoverEvent(@Nullable HoverEvent hoverEvent);

    List<Component> getSiblings();

    default boolean hasSiblings() {
        return getSiblings().size() > 0;
    }

    default Component append(String literal) {
        return append(literal(literal));
    }

    Component append(Component component);

    Component clearSiblings();

    default Component merge(Component other) {
        if (other.hasSiblings()) {
            clearSiblings();
            other.getSiblings().forEach(this::append);
        }
        if (other.getColor().isPresent())
            color(other.getColor().get());
        if (other.isBold().isPresent())
            bold(other.isBold().get());
        if (other.isItalic().isPresent())
            italic(other.isItalic().get());
        if (other.isUnderlined().isPresent())
            underlined(other.isUnderlined().get());
        if (other.isStrikethrough().isPresent())
            strikethrough(other.isStrikethrough().get());
        if (other.isObfuscated().isPresent())
            obfuscated(other.isObfuscated().get());
        if (other.getFont().isPresent())
            font(other.getFont().get());
        if (other.getInsertion().isPresent())
            insertion(other.getInsertion().get());
        if (other.getClickEvent().isPresent())
            clickEvent(other.getClickEvent().get());
        if (other.getHoverEvent().isPresent())
            hoverEvent(other.getHoverEvent().get());
        return this;
    }

    @Override
    default Map<String, Object> asMap() {
        Map<String, Object> map = new HashMap<>();
        if (getColor().isPresent())
            map.put("color", getColor().get().getName());
        if (isBold().isPresent())
            map.put("bold", isBold().get());
        if (isItalic().isPresent())
            map.put("italic", isItalic().get());
        if (isUnderlined().isPresent())
            map.put("underlined", isUnderlined().get());
        if (isStrikethrough().isPresent())
            map.put("strikethrough", isStrikethrough().get());
        if (isObfuscated().isPresent())
            map.put("obfuscated", isObfuscated().get());
        if (getInsertion().isPresent())
            map.put("insertion", getInsertion().get());
        if (getClickEvent().isPresent())
            map.put("clickEvent", getClickEvent().get().asMap());
        if (getHoverEvent().isPresent())
            map.put("hoverEvent", getHoverEvent().get().asMap());
        if (hasSiblings())
            map.put("extra", getSiblings().stream().map(Contents::asMap).toArray(Map[]::new));
        return map;
    }

    static Component literal(String literal) {
        return new TextComponent(literal);
    }

    static Component translation(String translation) {
        return new TranslationComponent(translation);
    }

    static Component keybind(String keybind) {
        return new KeybindComponent(keybind);
    }

}
