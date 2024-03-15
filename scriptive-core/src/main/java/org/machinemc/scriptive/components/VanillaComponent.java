package org.machinemc.scriptive.components;

/**
 * Represents a component that is supported by vanilla client.
 */
public sealed interface VanillaComponent
        extends Component
        permits KeybindComponent, TextComponent, TranslationComponent {
}
