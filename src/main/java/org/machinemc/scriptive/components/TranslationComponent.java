package org.machinemc.scriptive.components;

import java.util.Map;

public class TranslationComponent extends BaseComponent<String> {

    private String translation;
    private Component<?>[] arguments;

    protected TranslationComponent(String translation, Component<?>... arguments) {
        super();
        this.translation = translation;
        this.arguments = arguments;
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
        return arguments;
    }

    public void setArguments(Component<?>... arguments) {
        this.arguments = arguments;
    }

    @Override
    public Map<String, Object> asMap() {
        Map<String, Object> map = super.asMap();
        map.put("translate", translation);
        map.put("with", arguments);
        return map;
    }

    @Override
    public TranslationComponent clone() {
        TranslationComponent clone = new TranslationComponent(translation);
        clone.merge(this);
        return clone;
    }

    public static TranslationComponent of(String translation, Component<?>... arguments) {
        return new TranslationComponent(translation, arguments);
    }

}
