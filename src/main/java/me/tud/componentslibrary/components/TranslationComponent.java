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
    public void setValue(String translation) {
        this.translation = translation;
    }

    @Override
    public Map<String, Object> asMap() {
        Map<String, Object> map = super.asMap();
        map.put("translate", translation);
        return map;
    }

    @Override
    public TranslationComponent clone() {
        TranslationComponent clone = new TranslationComponent(translation);
        clone.merge(this);
        return clone;
    }

    public static TranslationComponent of(String translation) {
        return new TranslationComponent(translation);
    }

}
