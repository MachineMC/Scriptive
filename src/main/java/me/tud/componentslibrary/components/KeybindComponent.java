package me.tud.componentslibrary.components;

import java.util.Map;

public class KeybindComponent extends BaseComponent<String> {

    private String keybind;

    public KeybindComponent(String keybind) {
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
    public Component value(String keybind) {
        this.keybind = keybind;
        return this;
    }

    @Override
    public Map<String, Object> asMap() {
        Map<String, Object> map = super.asMap();
        map.put("keybind", keybind);
        return map;
    }

}
