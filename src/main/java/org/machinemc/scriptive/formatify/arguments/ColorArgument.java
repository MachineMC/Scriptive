package org.machinemc.scriptive.formatify.arguments;

import org.machinemc.scriptive.style.ChatColor;
import org.machinemc.scriptive.style.Colour;
import org.machinemc.scriptive.style.HexColor;
import org.machinemc.scriptive.formatify.Result;

import java.util.Locale;

public class ColorArgument implements Argument<Colour> {

    @Override
    public Result<Colour> parse(String unparsed) {
        if (HexColor.isValidHex(unparsed))
            return Result.of(HexColor.of(unparsed).orElse(null));

        try {
            ChatColor color = ChatColor.valueOf(unparsed.toUpperCase(Locale.ENGLISH));
            if (color != ChatColor.RESET)
                return Result.of(color);
        } catch (IllegalArgumentException ignore) {}
        return Result.error('\'' + unparsed + "' is not a valid color");
    }

}
