package me.tud.componentslibrary.components;

import java.util.Map;

public class KeybindComponent extends BaseComponent<String> {

    private String keybind;

    protected KeybindComponent(String keybind) {
        super();
        this.keybind = keybind;
    }

    public String getKeybind() {
        return keybind;
    }

    @Override
    public String getValue() {
        return keybind;
    }

    @Override
    public void setValue(String keybind) {
        this.keybind = keybind;
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

}
