package org.machinemc.scriptive.formatify.parameter;

import org.machinemc.scriptive.formatify.exceptions.ParseException;

public class EnumParameter<E extends Enum<E>> implements Parameter<E> {

    private final Class<E> enumClass;

    public EnumParameter(Class<E> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public E parse(String string) throws ParseException {
        for (E enumConstant : enumClass.getEnumConstants()) {
            if (string.equalsIgnoreCase(enumConstant.name()))
                return enumConstant;
        }
        throw new ParseException("Couldn't parse '" + string + "' as a " + enumClass.getSimpleName());
    }

}
