package org.machinemc.scriptive.formatify.parameter;

import org.machinemc.scriptive.formatify.exceptions.ParseException;

public class DoubleParameter implements Parameter<Double> {

    private final double min, max;

    public DoubleParameter() {
        this(Double.MIN_VALUE);
    }

    public DoubleParameter(double min) {
        this(min, Double.MAX_VALUE);
    }

    public DoubleParameter(double min, double max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public Double parse(String string) throws ParseException {
        try {
            double parsed = Double.parseDouble(string);
            if (parsed < min) throw new ParseException("Number must be lower than " + min);
            if (parsed > max) throw new ParseException("Number must be greater than " + max);
            return parsed;
        } catch (NumberFormatException e) {
            throw new ParseException(string + " is not a number", e);
        }
    }

    public double min() {
        return min;
    }

    public double max() {
        return max;
        
    }

}
