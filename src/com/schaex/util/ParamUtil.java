package com.schaex.util;

public final class ParamUtil {
    public static boolean isInRange(int value, int from, int to) {
        return from <= value && value < to;
    }

    private ParamUtil() {}
}
