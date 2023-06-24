package org.machinemc.scriptive.formatify.arguments;

import org.machinemc.scriptive.formatify.Result;

public interface Argument<T> {

    Result<T> parse(String unparsed);

}
