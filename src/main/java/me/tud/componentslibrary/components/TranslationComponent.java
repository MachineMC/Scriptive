package me.tud.componentslibrary.components;

import java.util.Map;

public class TranslationComponent extends BaseComponent<String> {

    private String translation;

    protected TranslationComponent(String translation) {
        super();
        this.translation = translation;
    }

    public String getTranslation() {
        return translation;
    }

    @Override
    public String getValue() {
        return translation;
    }

    @Override
    public Component value(String translation) {
        this.translation = translation;
        return this;
    }

    @Override
    public Map<String, Object> asMap() {
        Map<String, Object> map = super.asMap();
        map.put("translate", translation);
        return map;
    }

    public static TranslationComponent of(String translation) {
        return new TranslationComponent(translation);
    }

}
