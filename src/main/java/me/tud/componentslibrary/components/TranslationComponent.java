package me.tud.componentslibrary.components;

import java.util.Map;

public class TranslationComponent extends BaseComponent {

    private final String translation;

    protected TranslationComponent(String translation) {
        super();
        this.translation = translation;
    }

    public String getTranslation() {
        return translation;
    }

    @Override
    public Map<String, Object> asMap() {
        Map<String, Object> map = super.asMap();
        map.put("translate", translation);
        return map;
    }

}
