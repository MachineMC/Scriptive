package org.machinemc.scriptive.formatify.parameter;

import org.machinemc.scriptive.formatify.exceptions.ParseException;

public interface Parameter<T> {

    T parse(String string) throws ParseException;

}
