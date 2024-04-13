package org.machinemc.scriptive.components;

/**
 * Represents a component that is supported by vanilla client.
 */
public sealed interface ClientComponent
        extends Component
        permits KeybindComponent, TextComponent, TranslationComponent {

    ClientComponent clone();

    /**
     * @return whether all siblings of this component are constructed only from client components
     */
    default boolean isFullyClient() {
        for (Component child : getSiblings()) {
            if (!(child instanceof ClientComponent clientChild)) return false;
            if (!clientChild.isFullyClient()) return false;
        }
        return true;
    }

}
