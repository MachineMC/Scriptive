package org.machinemc.scriptive.formatify.tree;

import org.jetbrains.annotations.NotNull;
import org.machinemc.scriptive.components.TextComponent;

public class RootNode extends Node {

    public RootNode() {
        super(null);
    }

    @Override
    public @NotNull TextComponent evaluate() {
        return TextComponent.empty();
    }

}
