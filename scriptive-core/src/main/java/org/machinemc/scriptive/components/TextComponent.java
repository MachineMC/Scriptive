package org.machinemc.scriptive.components;

import org.machinemc.scriptive.style.TextFormat;

import java.util.Map;
import java.util.Objects;

public final class TextComponent extends BaseComponent implements VanillaComponent {

    private String text;

    private TextComponent(String text) {
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

    @Override
    public Class<TextComponent> getType() {
        return TextComponent.class;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TextComponent that)) return false;
        return text.equals(that.text)

                // base component
                && getSiblings().equals(that.getSiblings())
                && getTextFormat().equals(that.getTextFormat())
                && Objects.equals(getInsertion(), that.getInsertion())
                && Objects.equals(getClickEvent(), that.getClickEvent())
                && Objects.equals(getHoverEvent(), that.getHoverEvent());
    }

    @Override
    public int hashCode() {
        int result = text.hashCode();

        // base component
        result = 31 * result + getSiblings().hashCode();
        result = 31 * result + getTextFormat().hashCode();
        result = 31 * result + Objects.hashCode(getInsertion());
        result = 31 * result + Objects.hashCode(getClickEvent());
        result = 31 * result + Objects.hashCode(getHoverEvent());

        return result;
    }

    public static final class ComponentModifier extends Component.ComponentModifier<ComponentModifier, TextComponent> {

        private ComponentModifier(TextComponent component) {
            super(component);
        }

        public ComponentModifier text(String text) {
            component.setText(text);
            return getThis();
        }

    }

}
