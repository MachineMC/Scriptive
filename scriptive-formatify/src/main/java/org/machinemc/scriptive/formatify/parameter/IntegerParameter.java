package org.machinemc.scriptive.formatify.parameter;

import org.machinemc.scriptive.formatify.exceptions.ParseException;

public class IntegerParameter implements Parameter<Integer> {

    private final int min, max;

    public IntegerParameter() {
        this(Integer.MIN_VALUE);
    }

    public IntegerParameter(int min) {
        this(min, Integer.MAX_VALUE);
    }

    public IntegerParameter(int min, int max) {
        if (max <= min)
            throw new IllegalArgumentException("max must be greater than min");
        this.min = min;
        this.max = max;
    }

    @Override
    public Integer parse(String string) throws ParseException {
        try {
            int parsed = Integer.parseInt(string);
            if (parsed < min) throw new ParseException("Integer must be lower than " + min);
            if (parsed > max) throw new ParseException("Integer must be greater than " + max);
            return parsed;
        } catch (NumberFormatException e) {
            throw new ParseException(string + " is not an integer", e);
        }
    }

    public int min() {
        return min;
    }

    public int max() {
        return max;
    }

}
