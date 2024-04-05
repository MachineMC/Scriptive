package org.machinemc.scriptive.serialization;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.UnmodifiableView;

/**
 * Represents an object that can be serialized as component properties.
 * <p>
 * This could be anything that is part of component.
 */
public interface Contents {

    /**
     * Returns component properties representation of this object.
     * <p>
     * The given component properties are not modifiable.
     *
     * @return properties of this content
     */
    @Contract("-> new")
    @UnmodifiableView ComponentProperties getProperties();

}
