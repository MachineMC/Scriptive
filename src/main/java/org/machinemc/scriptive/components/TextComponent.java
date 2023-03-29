package org.machinemc.scriptive.components;

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
    public void setValue(String text) {
        this.text = text;
    }

    @Override
    public Map<String, Object> asMap() {
        Map<String, Object> map = super.asMap();
        map.put("text", text);
        return map;
    }

    @Override
    public TextComponent clone() {
        TextComponent clone = new TextComponent(text);
        clone.merge(this);
        return clone;
    }

    public static TextComponent of(String text) {
        return new TextComponent(text);
    }

}
