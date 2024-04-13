package org.machinemc.scriptive.style;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import org.machinemc.scriptive.serialization.ComponentProperties;
import org.machinemc.scriptive.serialization.Contents;

import java.util.*;

/**
 * Represents format which can be applied to a {@link org.machinemc.scriptive.components.Component}.
 * <p>
 * All properties of text format are optional.
 */
public final class TextFormat implements Contents {

    private @Nullable Colour color;
    private @Nullable String font;
    private final Map<ChatStyle, @Nullable Boolean> styleMap = new HashMap<>();

    /**
     * Creates new text format from existing component properties.
     *
     * @param properties properties
     */
    public TextFormat(ComponentProperties properties) {
        color = properties.getValue("color", String.class).map(Colour::fromName).orElse(null);
        font = properties.getValue("font", String.class).orElse(null);
        styleMap.clear();
        Arrays.stream(ChatStyle.values()).forEach(style -> {
            Boolean value = properties.getValue(style.getName(), Boolean.class).orElse(null);
            if (value == null) return;
            styleMap.put(style, value);
        });
    }

    /**
     * Creates new format from multiple chat styles.
     *
     * @param styles styles for this text format
     */
    public TextFormat(ChatStyle... styles) {
        this(null, styles);
    }

    /**
     * Creates new chat format from color and chat styles.
     *
     * @param color color
     * @param styles styles
     */
    public TextFormat(@Nullable Colour color, ChatStyle... styles) {
        this(color, null, stylesToMap(styles));
    }

    /**
     * Creates new chat format from color, font, and chat styles.
     *
     * @param color color
     * @param font font
     * @param styles styles
     */
    public TextFormat(@Nullable Colour color, @Nullable String font, ChatStyle... styles) {
        this(color, font, stylesToMap(styles));
    }

    /**
     * Creates new chat format from color, font, and chat styles.
     *
     * @param color color
     * @param font font
     * @param styleMap styles
     */
    public TextFormat(@Nullable Colour color, @Nullable String font, Map<ChatStyle, @Nullable Boolean> styleMap) {
        this.color = color;
        this.font = font;
        styleMap.forEach((style, flag) -> {
            if (style == null || flag == null) return;
            this.styleMap.put(style, flag);
        });
    }

    /**
     * @return color
     */
    public Optional<Colour> getColor() {
        return Optional.ofNullable(color);
    }

    /**
     * @param color new color
     */
    public void setColor(@Nullable Colour color) {
        this.color = color;
    }

    /**
     * @return font
     */
    public Optional<String> getFont() {
        return Optional.ofNullable(font);
    }

    /**
     * @param font new font
     */
    public void setFont(@Nullable String font) {
        this.font = font;
    }

    /**
     * @param style style
     * @return value for given style
     */
    public Optional<Boolean> getStyle(ChatStyle style) {
        Objects.requireNonNull(style, "Style can not be null");
        return Optional.ofNullable(styleMap.get(style));
    }

    /**
     * @param style style
     * @param flag new value for given style
     */
    public void setStyle(ChatStyle style, @Nullable Boolean flag) {
        Objects.requireNonNull(style, "Style can not be null");
        styleMap.put(style, flag);
    }

    /**
     * Returns unmodifiable map view for this text format.
     *
     * @return map view of this text format
     */
    public @UnmodifiableView Map<ChatStyle, @Nullable Boolean> getStyles() {
        return Collections.unmodifiableMap(styleMap);
    }

    /**
     * Returns all styles of this text format with given value.
     *
     * @param flag value
     * @return chat styles with given value
     */
    public ChatStyle[] getStyles(@Nullable Boolean flag) {
        List<ChatStyle> list = new ArrayList<>();
        getStyles().forEach((style, aBoolean) -> {
            if (Objects.equals(flag, aBoolean))
                list.add(style);
        });
        return list.toArray(new ChatStyle[0]);
    }

    /**
     * Inherits values of this text format from another text format.
     * <p>
     * Does not override already set values.
     *
     * @param parent parent format
     */
    public void inheritFrom(TextFormat parent) {
        Objects.requireNonNull(parent, "Parent text format can not be null");
        getColor().ifPresentOrElse(k -> {}, () -> setColor(parent.color));
        getFont().ifPresentOrElse(k -> {}, () -> setFont(parent.font));
        parent.styleMap.forEach((style, flag) -> {
            if (flag == null || getStyle(style).isPresent()) return;
            setStyle(style, flag);
        });
    }

    /**
     * Merges text format from another text format.
     *
     * @param other other text format
     */
    public void merge(TextFormat other) {
        Objects.requireNonNull(other, "Other text format can not be null");
        other.getColor().ifPresent(this::setColor);
        other.getFont().ifPresent(this::setFont);
        other.styleMap.forEach((style, flag) -> {
            if (flag != null)
                styleMap.put(style, flag);
        });
    }

    /**
     * Copies all from another text format to this one.
     *
     * @param other other text format
     */
    public void copy(TextFormat other) {
        Objects.requireNonNull(other, "Other text format can not be null");
        setColor(other.getColor().orElse(null));
        setFont(other.getFont().orElse(null));
        styleMap.clear();
        other.styleMap.forEach(this::setStyle);
    }

    private static Map<ChatStyle, @Nullable Boolean> stylesToMap(ChatStyle... styles) {
        Map<ChatStyle, @Nullable Boolean> styleMap = new HashMap<>();
        for (ChatStyle style : styles) {
            Objects.requireNonNull(style, "Style can not be null");
            styleMap.put(style, true);
        }
        return styleMap;
    }

    @Override
    public ComponentProperties getProperties() {
        ComponentProperties properties = new ComponentProperties();
        properties.set("color", color != null ? color.getName() : null);
        properties.set("font", font);
        styleMap.forEach((style, flag) -> properties.set(style.getName(), flag));
        return properties.unmodifiableView();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "TextFormat[", "]")
                .add("color=" + color)
                .add("font='" + font + "'")
                .add("styleMap=" + styleMap)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TextFormat that)) return false;
        return Objects.equals(color, that.color) && Objects.equals(font, that.font) && styleMap.equals(that.styleMap);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(color);
        result = 31 * result + Objects.hashCode(font);
        result = 31 * result + styleMap.hashCode();
        return result;
    }

}
