package org.machinemc.scriptive.formatify.parameter;

import org.machinemc.scriptive.formatify.exceptions.ParseException;

public class StringParameter implements Parameter<String> {

    @Override
    public String parse(String string) throws ParseException {
        return string;
    }

}
