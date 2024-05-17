package org.machinemc.scriptive.formatify.parser.node;

import org.machinemc.scriptive.components.TextComponent;

public class RootNode extends Node {

    public RootNode() {
        super(null);
    }

    @Override
    public TextComponent evaluate() {
        TextComponent root = TextComponent.empty();
        for (Node child : children())
            root.append(asFullComponent(child));
        return root;
    }

    private static TextComponent asFullComponent(Node node) {
        TextComponent component = node.evaluate();
        for (Node child : node.children())
            component.append(asFullComponent(child));
        return component;
    }

}
