package org.machinemc.scriptive.serialization;

import org.machinemc.scriptive.components.Component;

import java.util.Map;

@FunctionalInterface
public interface ComponentCreator<C extends Component> {

    C create(Map<String, Object> properties);

}
