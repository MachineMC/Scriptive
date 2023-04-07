package org.machinemc.scriptive.components;

import org.machinemc.scriptive.Contents;

import java.util.Arrays;
import java.util.Map;

public class TranslationComponent extends BaseComponent {

    private String translation;
    private Component[] arguments;

    protected TranslationComponent(String translation, Component... arguments) {
        super();
        this.translation = translation;
        this.arguments = arguments;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public Component[] getArguments() {
        return arguments;
    }

    public void setArguments(Component... arguments) {
        this.arguments = arguments;
    }

    @Override
    public String flatten() {
        return translation;
    }

    @Override
    public void merge(Component other) {
        super.merge(other);
        if (getClass().isInstance(other)) {
            setTranslation(((TranslationComponent) other).getTranslation());
            setArguments(((TranslationComponent) other).getArguments());
        }
    }

    @Override
    public ComponentModifier modify() {
        return new ComponentModifier(this);
    }

    @Override
    public Map<String, Object> asMap() {
        Map<String, Object> map = super.asMap();
        map.put("translate", translation);
        map.put("with", Arrays.stream(arguments).map(Contents::asMap).toArray(Map[]::new));
        return map;
    }

    @Override
    public TranslationComponent clone() {
        TranslationComponent clone = new TranslationComponent(translation);
        clone.merge(this);
        return clone;
    }

    public static TranslationComponent of(String translation, Component... arguments) {
        return new TranslationComponent(translation, arguments);
    }

    public static class ComponentModifier extends Component.ComponentModifier<TranslationComponent> {

        protected ComponentModifier(TranslationComponent component) {
            super(component);
        }

        public ComponentModifier translation(String translation) {
            clone.setTranslation(translation);
            return this;
        }

        public ComponentModifier arguments(Component... arguments) {
            clone.setArguments(arguments);
            return this;
        }

    }

}
