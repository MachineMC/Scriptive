package org.machinemc.scriptive.transform;

import org.machinemc.scriptive.components.Component;

public interface ComponentTransformer<I extends Component, O extends Component> {

    O transform(I input);

    Class<I> getInputType();

    Class<O> getOutputType();

}
