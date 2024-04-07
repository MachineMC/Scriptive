package org.machinemc.scriptive.components;

import org.jetbrains.annotations.UnmodifiableView;
import org.machinemc.scriptive.serialization.ComponentProperties;
import org.machinemc.scriptive.serialization.ComponentSerializer;
import org.machinemc.scriptive.style.TextFormat;

import java.util.List;
import java.util.Objects;

/**
 * A component that displays the current keybind for an action.
 */
public final class KeybindComponent extends BaseComponent implements ClientComponent {

    /**
     * Creates new keybind component.
     *
     * @param keybind keybind
     * @return keybind component
     */
    public static KeybindComponent of(String keybind) {
        return new KeybindComponent(keybind);
    }

    private String keybind;

    private KeybindComponent(String keybind) {
        this.keybind = Objects.requireNonNull(keybind, "Keybind can not be null");
    }

    /**
     * @return keybind
     */
    public String getKeybind() {
        return keybind;
    }

    /**
     * @param keybind new keybind
     */
    public void setKeybind(String keybind) {
        this.keybind = keybind;
    }

    @Override
    public String getName() {
        return "keybind";
    }

    @Override
    public List<String> getUniqueKeys() {
        return List.of("keybind");
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
    public KeybindComponent clone() {
        KeybindComponent clone = new KeybindComponent(keybind);
        clone.merge(this);
        return clone;
    }

    @Override
    public Class<KeybindComponent> getType() {
        return KeybindComponent.class;
    }

    @Override
    public void loadProperties(ComponentProperties properties, ComponentSerializer<?> serializer) {
        super.loadProperties(properties, serializer);
        keybind = properties.getValue("keybind", String.class).orElseThrow();
    }

    @Override
    public @UnmodifiableView ComponentProperties getProperties() {
        ComponentProperties properties = super.getProperties();
        properties.set("keybind", keybind);
        return properties.unmodifiableView();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KeybindComponent that)) return false;
        return keybind.equals(that.keybind)

                // base component
                && getSiblings().equals(that.getSiblings())
                && getTextFormat().equals(that.getTextFormat())
                && Objects.equals(getInsertion(), that.getInsertion())
                && Objects.equals(getClickEvent(), that.getClickEvent())
                && Objects.equals(getHoverEvent(), that.getHoverEvent());
    }

    @Override
    public int hashCode() {
        int result = keybind.hashCode();

        // base component
        result = 31 * result + getSiblings().hashCode();
        result = 31 * result + getTextFormat().hashCode();
        result = 31 * result + Objects.hashCode(getInsertion());
        result = 31 * result + Objects.hashCode(getClickEvent());
        result = 31 * result + Objects.hashCode(getHoverEvent());

        return result;
    }

    public static final class ComponentModifier extends Component.ComponentModifier<ComponentModifier, KeybindComponent> {

        private ComponentModifier(KeybindComponent component) {
            super(component);
        }

        public ComponentModifier keybind(String keybind) {
            component.setKeybind(keybind);
            return getThis();
        }

    }

}
