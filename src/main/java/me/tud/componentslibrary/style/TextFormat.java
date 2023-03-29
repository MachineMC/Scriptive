package me.tud.componentslibrary.style;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

public class TextFormat {

    private @Nullable Colour color;
    private final Map<ChatStyle, @Nullable Boolean> styleMap;

    public TextFormat(ChatStyle... styles) {
        this(null, styles);
    }

    public TextFormat(@Nullable Colour color, ChatStyle... styles) {
        this(color, stylesToMap(styles));
    }

    public TextFormat(@Nullable Colour color, Map<ChatStyle, @Nullable Boolean> styleMap) {
        this.color = color;
        this.styleMap = styleMap;
    }

    public Optional<Colour> getColor() {
        return Optional.ofNullable(color);
    }

    public void setColor(@Nullable Colour color) {
        this.color = color;
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

    private static Map<ChatStyle, @Nullable Boolean> stylesToMap(ChatStyle... styles) {
        Map<ChatStyle, @Nullable Boolean> styleMap = new HashMap<>();
        for (ChatStyle style : styles)
            styleMap.put(style, true);
        return styleMap;
    }

}
