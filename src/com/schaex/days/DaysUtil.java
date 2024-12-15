package com.schaex.days;

import java.io.InputStream;

public final class DaysUtil {
    static final boolean JUST_SHOW_RESULTS = true;

    private DaysUtil() {}

    public static InputStream resource(String name) {
        return DaysUtil.class.getResourceAsStream(name);
    }

    public static InputStream resource(int day) {
        final int absVal = Math.abs(day);
        String prefix = absVal < 10 ? "Day_0" + absVal : "Day_" + absVal;

        if (day < 0) {
            prefix += "_test";
        }

        return resource(prefix + ".txt");
    }
}
