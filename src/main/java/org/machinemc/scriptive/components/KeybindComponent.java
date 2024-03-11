package org.machinemc.scriptive.components;

import org.machinemc.scriptive.style.TextFormat;

import java.util.Map;
import java.util.Objects;

public class KeybindComponent extends BaseComponent {

    private String keybind;

    protected KeybindComponent(String keybind) {
        super();
        this.keybind = keybind;
    }

    public String getKeybind() {
        return keybind;
    }

    public void setKeybind(String keybind) {
        this.keybind = keybind;
    }

    @Override
    public String getString() {
        return keybind;
    }

    @Override
    public KeybindComponent append(String literal) {
        return (KeybindComponent) super.append(literal);
    }

    @Override
    public KeybindComponent append(String literal, TextFormat textFormat) {
        return (KeybindComponent) super.append(literal, textFormat);
    }

    @Override
    public KeybindComponent append(Component component) {
        return (KeybindComponent) super.append(component);
    }

    @Override
    public void merge(Component other) {
        super.merge(other);
        if (getClass().isInstance(other))
            setKeybind(((KeybindComponent) other).getKeybind());
    }

    @Override
    public ComponentModifier modify() {
        return new ComponentModifier(this);
    }

    @Override
    public Map<String, Object> asMap() {
        Map<String, Object> map = super.asMap();
        map.put("keybind", keybind);
        return map;
    }

    @Override
    public KeybindComponent clone() {
        KeybindComponent clone = new KeybindComponent(keybind);
        clone.merge(this);
        return clone;
    }

    public static KeybindComponent of(String keybind) {
        return new KeybindComponent(keybind);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KeybindComponent that)) return false;
        return keybind.equals(that.keybind)

                // base component
                && getSiblings().equals(that.getSiblings())
                && getTextFormat().equals(that.getTextFormat())
                && Objects.equals(getInsertion(), that.getInsertion())
                && Objects.equals(getClickEvent(), that.getClickEvent())
                && Objects.equals(getHoverEvent(), that.getHoverEvent());
    }

    @Override
    public int hashCode() {
        int result = keybind.hashCode();

        // base component
        result = 31 * result + getSiblings().hashCode();
        result = 31 * result + getTextFormat().hashCode();
        result = 31 * result + Objects.hashCode(getInsertion());
        result = 31 * result + Objects.hashCode(getClickEvent());
        result = 31 * result + Objects.hashCode(getHoverEvent());

        return result;
    }

    public static class ComponentModifier extends Component.ComponentModifier<ComponentModifier, KeybindComponent> {

        protected ComponentModifier(KeybindComponent component) {
            super(component);
        }

        public ComponentModifier keybind(String keybind) {
            component.setKeybind(keybind);
            return getThis();
        }

        @Override
        protected ComponentModifier getThis() {
            return this;
        }

    }

}
