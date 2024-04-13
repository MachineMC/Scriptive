package org.machinemc.scriptive.formatify.tree;

import org.jetbrains.annotations.NotNull;
import org.machinemc.scriptive.components.TextComponent;

import java.util.ArrayList;
import java.util.List;

public abstract class Node {

    private final Node parent;
    private final List<Node> children = new ArrayList<>();

    public Node(Node parent) {
        this.parent = parent;
    }

    public Node getParent() {
         return parent;
    }

    public @NotNull List<Node> getChildren() {
        return children;
    }

    public void addChild(Node node) {
        children.add(node);
    }

    public abstract @NotNull TextComponent evaluate();

}
