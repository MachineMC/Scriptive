package org.machinemc.scriptive.transform;

import org.machinemc.scriptive.components.Component;
import org.machinemc.scriptive.components.VanillaComponent;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

// TODO transform all siblings in #transform method
public class ComponentTransformerPipeline {

    private final Map<Class<? extends Component>, ComponentTransformer<?, ?>> transformers = new ConcurrentHashMap<>();

    public void register(ComponentTransformer<?, ?> transformer) {
        if (transformers.containsKey(transformer.getInputType()))
            throw new IllegalArgumentException("Transformer for '" + transformer.getInputType().getName() + "' components is already registered");
        transformers.put(transformer.getInputType(), transformer);
    }

    public boolean unregister(ComponentTransformer<?, ?> transformer) {
        return transformers.remove(transformer.getInputType(), transformer);
    }

    public boolean containsTransformer(ComponentTransformer<?, ?> transformer) {
        return containsTransformer(transformer.getInputType());
    }

    public boolean containsTransformer(Class<? extends Component> inputType) {
        return transformers.containsKey(inputType);
    }

    public VanillaComponent transform(Component component) {
        return transform(component, new HashSet<>());
    }

    @SuppressWarnings("unchecked")
    private VanillaComponent transform(Component component, Set<ComponentTransformer<?, ?>> used) {
        if (component instanceof VanillaComponent) return (VanillaComponent) component;
        ComponentTransformer<Component, Component> next = (ComponentTransformer<Component, Component>) transformers.get(component.getType());
        if (next == null)
            throw new NullPointerException("Missing transformer for '" + component.getType().getName() + "' component");
        if (used.contains(next))
            throw new RuntimeException("Cyclic inheritance error for '" + component.getType().getName() + "' component");
        used.add(next);
        return transform(next.transform(component), used);
    }

}
