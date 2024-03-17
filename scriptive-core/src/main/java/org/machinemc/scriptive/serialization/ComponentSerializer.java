package org.machinemc.scriptive.serialization;

import org.machinemc.scriptive.components.ClientComponent;

public interface ComponentSerializer<R> {

    ClientComponent deserialize(R input);

    R serialize(ClientComponent component);

}
