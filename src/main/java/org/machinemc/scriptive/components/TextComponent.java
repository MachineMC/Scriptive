package org.machinemc.scriptive.components;

import org.machinemc.scriptive.style.TextFormat;

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
    public String getString() {
        return text;
    }

    @Override
    public TextComponent append(String literal) {
        return (TextComponent) super.append(literal);
    }

    @Override
    public TextComponent append(String literal, TextFormat textFormat) {
        return (TextComponent) super.append(literal, textFormat);
    }

    @Override
    public TextComponent append(Component component) {
        return (TextComponent) super.append(component);
    }

    @Override
    public void merge(Component other) {
        super.merge(other);
        if (other instanceof TextComponent textComponent)
            setText(textComponent.getText());
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

    public static TextComponent of(String text, TextFormat textFormat) {
        TextComponent component = new TextComponent(text);
        component.setTextFormat(textFormat);
        return component;
    }

    public static TextComponent empty() {
        return of("");
    }

    public static class ComponentModifier extends Component.ComponentModifier<ComponentModifier, TextComponent> {

        protected ComponentModifier(TextComponent component) {
            super(component);
        }

        public ComponentModifier text(String text) {
            component.setText(text);
            return getThis();
        }

        @Override
        protected ComponentModifier getThis() {
            return this;
        }

    }

}
