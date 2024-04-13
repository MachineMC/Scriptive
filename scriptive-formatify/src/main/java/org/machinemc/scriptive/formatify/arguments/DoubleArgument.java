package org.machinemc.scriptive.formatify.arguments;

import org.machinemc.scriptive.formatify.Result;

public class DoubleArgument implements Argument<Double> {

    private final double min, max;

    public DoubleArgument() {
        this(Double.MIN_VALUE, Double.MAX_VALUE);
    }

    public DoubleArgument(double min) {
        this(min, Double.MAX_VALUE);
    }

    public DoubleArgument(double min, double max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public Result<Double> parse(String unparsed) {
        try {
            double d = Double.parseDouble(unparsed);
            if (d < min)
                return Result.error("The double cannot be less than " + min);
            if (d > max)
                return Result.error("The double cannot be more than " + max);
            return Result.of(d);
        } catch (NumberFormatException e) {
            return Result.error('\'' + unparsed + "' is not a valid double");
        }
    }

}
