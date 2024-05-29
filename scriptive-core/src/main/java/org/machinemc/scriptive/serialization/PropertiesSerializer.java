package org.machinemc.scriptive.serialization;

public interface PropertiesSerializer<T> {

    /**
     * Serializes component properties.
     *
     * @param properties component properties
     * @return value
     */
    T serialize(ComponentProperties properties);

    /**
     * Deserializes to component properties.
     *
     * @param value value
     * @return component properties
     */
    ComponentProperties deserialize(T value);

}
