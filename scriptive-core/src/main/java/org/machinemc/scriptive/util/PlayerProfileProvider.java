package org.machinemc.scriptive.util;

import org.jetbrains.annotations.NotNull;
import org.machinemc.scriptive.serialization.ComponentProperties;

/**
 * A provider for player profile properties.
 * <br>
 * The properties must match the structure mentioned <a href="https://minecraft.wiki/w/Data_component_format#profile">here</a>
 */
public interface PlayerProfileProvider {

    /**
     * @return player profile properties
     */
    @NotNull ComponentProperties profileProperties();

}
