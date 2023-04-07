package org.machinemc.scriptive.components;

import java.util.Map;

public class TextComponent extends BaseComponent {

    private String text;

    protected TextComponent(String text) {
        super();
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String flatten() {
        return text;
    }

    @Override
    public void merge(Component other) {
        super.merge(other);
        if (getClass().isInstance(other))
            setText(((TextComponent) other).getText());
    }

    @Override
    public ComponentModifier modify() {
        return new ComponentModifier(this);
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

    public static TextComponent empty() {
        return of("");
    }

    public static class ComponentModifier extends Component.ComponentModifier<TextComponent> {

        protected ComponentModifier(TextComponent component) {
            super(component);
        }

        public ComponentModifier text(String text) {
            clone.setText(text);
            return this;
        }

    }

}
