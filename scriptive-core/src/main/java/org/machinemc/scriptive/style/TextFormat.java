package org.machinemc.scriptive.style;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import org.machinemc.scriptive.Contents;

import java.util.*;

public class TextFormat implements Contents {

    private @Nullable Colour color;
    private @Nullable String font;
    private final Map<ChatStyle, @Nullable Boolean> styleMap;

    public TextFormat(ChatStyle... styles) {
        this(null, styles);
    }

    public TextFormat(@Nullable Colour color, ChatStyle... styles) {
        this(color, null, stylesToMap(styles));
    }

    public TextFormat(@Nullable Colour color, @Nullable String font, ChatStyle... styles) {
        this(color, font, stylesToMap(styles));
    }

    public TextFormat(@Nullable Colour color, @Nullable String font, Map<ChatStyle, @Nullable Boolean> styleMap) {
        this.color = color;
        this.font = font;
        this.styleMap = styleMap;
    }

    public Optional<Colour> getColor() {
        return Optional.ofNullable(color);
    }

    public void setColor(@Nullable Colour color) {
        this.color = color;
    }

    public Optional<String> getFont() {
        return Optional.ofNullable(font);
    }

    public void setFont(@Nullable String font) {
        this.font = font;
    }

    public Optional<Boolean> getStyle(ChatStyle style) {
        return Optional.ofNullable(styleMap.get(style));
    }

    public void setStyle(ChatStyle style, @Nullable Boolean flag) {
        styleMap.put(style, flag);
    }

    public @UnmodifiableView Map<ChatStyle, @Nullable Boolean> getStyles() {
        return Collections.unmodifiableMap(styleMap);
    }

    public ChatStyle[] getStyles(@Nullable Boolean flag) {
        List<ChatStyle> list = new ArrayList<>();
        getStyles().forEach((style, aBoolean) -> {
            if (Objects.equals(flag, aBoolean))
                list.add(style);
        });
        return list.toArray(new ChatStyle[0]);
    }

    public void inheritFrom(TextFormat parent) {
        getColor().ifPresentOrElse(k -> {}, () -> setColor(parent.color));
        getFont().ifPresentOrElse(k -> {}, () -> setFont(parent.font));
        parent.styleMap.forEach((chatStyle, flag) -> {
            if (flag == null || getStyle(chatStyle).isPresent()) return;
            setStyle(chatStyle, flag);
        });
    }

    public void merge(TextFormat other) {
        other.getColor().ifPresent(this::setColor);
        other.getFont().ifPresent(this::setFont);
        other.styleMap.forEach((chatStyle, flag) -> {
            if (flag != null)
                styleMap.put(chatStyle, flag);
        });
    }

    private static Map<ChatStyle, @Nullable Boolean> stylesToMap(ChatStyle... styles) {
        Map<ChatStyle, @Nullable Boolean> styleMap = new HashMap<>();
        for (ChatStyle style : styles)
            styleMap.put(style, true);
        return styleMap;
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
    public Map<String, Object> asMap() {
        Map<String, Object> map = new HashMap<>();
        getColor().ifPresent(colour -> map.put("color", colour.getName()));
        getFont().ifPresent(font -> map.put("font", font));
        getStyles().forEach((chatStyle, flag) -> {
            if (flag != null)
                map.put(chatStyle.getName(), flag);
        });
        return map;
    }

    @Override
    public final boolean equals(Object o) {
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
