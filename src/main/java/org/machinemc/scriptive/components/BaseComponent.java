package org.machinemc.scriptive.components;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import org.machinemc.scriptive.Contents;
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
    public void merge(Component other) {
        if (other.hasSiblings()) {
            clearSiblings();
            other.getSiblings().forEach(sibling -> append(sibling.clone()));
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

    @Override
    public TextFormat getFormat() {
        Map<ChatStyle, @Nullable Boolean> map = new HashMap<>();
        isObfuscated().ifPresent(obfuscated -> map.put(ChatStyle.OBFUSCATED, obfuscated));
        isBold().ifPresent(bold -> map.put(ChatStyle.BOLD, bold));
        isStrikethrough().ifPresent(strikethrough -> map.put(ChatStyle.STRIKETHROUGH, strikethrough));
        isUnderlined().ifPresent(underlined -> map.put(ChatStyle.UNDERLINED, underlined));
        isItalic().ifPresent(italic -> map.put(ChatStyle.ITALIC, italic));
        return new TextFormat(getColor().orElse(null), map);
    }

    @Override
    public void applyFormat(TextFormat format) {
        format.getColor().ifPresent(this::setColor);
        format.getStyle(ChatStyle.BOLD).ifPresent(this::setBold);
        format.getStyle(ChatStyle.OBFUSCATED).ifPresent(this::setObfuscated);
        format.getStyle(ChatStyle.STRIKETHROUGH).ifPresent(this::setStrikethrough);
        format.getStyle(ChatStyle.UNDERLINED).ifPresent(this::setUnderlined);
        format.getStyle(ChatStyle.ITALIC).ifPresent(this::setItalic);
    }

    @Override
    public String toLegacyString() {
        List<Component> components = separatedComponents();
        StringBuilder builder = new StringBuilder();
        for (Component component : components)
            builder.append(toLegacyString(component));
        return builder.toString();
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

    @Override
    public List<Component> separatedComponents() {
        List<Component> components = new LinkedList<>();
        addSeparatedComponents(clone(), components);
        return components;
    }

    private static void addSeparatedComponents(Component parent, List<Component> components) {
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
            addSeparatedComponents(child, components);
        }
        parent.clearSiblings();
    }

    @Override
    public abstract BaseComponent clone();

    @Override
    public Map<String, Object> asMap() {
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

}
