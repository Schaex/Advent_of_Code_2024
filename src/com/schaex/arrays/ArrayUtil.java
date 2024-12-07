package com.schaex.arrays;

import java.lang.reflect.Array;

public final class ArrayUtil {
    private ArrayUtil() {}

    @SuppressWarnings("unchecked")
    public static <T> T[][] transposeRectangular(T[][] array) {
        final Class<T> clazz = (Class<T>) array.getClass().componentType().componentType();
        final T[][] transposed = (T[][]) Array.newInstance(clazz, array[0].length, array.length);

        for (int i = 0; i < array.length; i++) {
            final T[] inner = array[i];

            for (int j = 0; j < inner.length; j++) {
                transposed[j][i] = inner[j];
            }
        }

        return transposed;
    }

    public static int[][] transposeRectangular(int[][] array) {
        final int[][] transposed = new int[array[0].length][array.length];

        for (int i = 0; i < array.length; i++) {
            final int[] inner = array[i];

            for (int j = 0; j < inner.length; j++) {
                transposed[j][i] = inner[j];
            }
        }

        return transposed;
    }

    public static int[] intArrayFromStrings(String[] strArray) {
        final int[] ints = new int[strArray.length];

        for (int i = 0; i < ints.length; i++) {
            ints[i] = Integer.parseInt(strArray[i]);
        }

        return ints;
    }

    public static long[] longArrayFromStrings(String[] strArray) {
        final long[] longs = new long[strArray.length];

        for (int i = 0; i < longs.length; i++) {
            longs[i] = Long.parseLong(strArray[i]);
        }

        return longs;
    }
}
