package me.tud.componentslibrary.components;

import java.util.Map;

public class TextComponent extends BaseComponent<String> {

    private String text;

    protected TextComponent(String text) {
        super();
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public String getValue() {
        return text;
    }

    @Override
    public Component value(String text) {
        this.text = text;
        return this;
    }

    @Override
    public Map<String, Object> asMap() {
        Map<String, Object> map = super.asMap();
        map.put("text", text);
        return map;
    }

    public static TextComponent of(String text) {
        return new TextComponent(text);
    }

}
