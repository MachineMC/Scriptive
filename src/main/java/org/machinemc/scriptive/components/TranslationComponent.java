package org.machinemc.scriptive.components;

import java.util.Map;

public class TranslationComponent extends BaseComponent<String> {

    private String translation;
    private Component<?>[] with;

    protected TranslationComponent(String translation, Component<?>... with) {
        super();
        this.translation = translation;
        this.with = with;
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

    public Component<?>[] getArguments() {
        return with;
    }

    public void setArguments(Component<?>... arguments) {
        this.with = arguments;
    }

    @Override
    public Map<String, Object> asMap() {
        Map<String, Object> map = super.asMap();
        map.put("translate", translation);
        map.put("with", with);
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
