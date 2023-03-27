package me.tud.componentslibrary.components;

import java.util.Map;

public class KeybindComponent extends BaseComponent {

    private final String keybind;

    protected KeybindComponent(String keybind) {
        super();
        this.keybind = keybind;
    }

    public String getKeybind() {
        return keybind;
    }

    @Override
    public Map<String, Object> asMap() {
        Map<String, Object> map = super.asMap();
        map.put("keybind", keybind);
        return map;
    }

}
