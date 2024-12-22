package com.schaex.util;

public final class ParamUtil {
    public static boolean isInRange(int value, int from, int to) {
        return from <= value && value < to;
    }

    public static int clamp(int value, int min, int max) {
        if (value < min) {
            return min;
        }

        return Math.min(value, max);
    }

    private ParamUtil() {}
}
