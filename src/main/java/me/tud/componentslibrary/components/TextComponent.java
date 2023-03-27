package me.tud.componentslibrary.components;

import java.util.Map;

public class TextComponent extends BaseComponent {

    private final String literal;

    protected TextComponent(String literal) {
        super();
        this.literal = literal;
    }

    public String getLiteral() {
        return literal;
    }

    @Override
    public Map<String, Object> asMap() {
        Map<String, Object> map = super.asMap();
        map.put("text", literal);
        return map;
    }

}
