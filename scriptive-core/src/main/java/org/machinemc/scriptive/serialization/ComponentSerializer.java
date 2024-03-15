package org.machinemc.scriptive.serialization;

import org.machinemc.scriptive.components.VanillaComponent;

public interface ComponentSerializer<R> {

    VanillaComponent deserialize(R input);

    R serialize(VanillaComponent component);

}
