package org.machinemc.scriptive.components;

import org.machinemc.scriptive.style.TextFormat;

import java.util.Map;

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
