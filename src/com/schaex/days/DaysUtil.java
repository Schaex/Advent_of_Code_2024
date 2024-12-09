package com.schaex.days;

import java.io.File;
import java.util.Objects;

public final class DaysUtil {
    private DaysUtil() {}

    public static File resource(String name) {
        return new File(Objects.requireNonNull(DaysUtil.class.getResource(name)).getFile());
    }

    public static File resource(int day) {
        final int absVal = Math.abs(day);
        String prefix = absVal < 10 ? "Day_0" + absVal : "Day_" + absVal;

        if (day < 0) {
            prefix += "_test";
        }

        return resource(prefix + ".txt");
    }
}
