package com.schaex.days;

import java.io.File;
import java.util.Objects;

public final class DaysUtil {
    private DaysUtil() {}

    public static File resource(String name) {
        return new File(Objects.requireNonNull(DaysUtil.class.getResource(name)).getFile());
    }

    public static File resource(int day) {
        String num = day < 10 ? "0" + day : Integer.toString(day);

        return resource("Day_" + num + ".txt");
    }
}
