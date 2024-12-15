package com.schaex.arrays;

import com.schaex.util.ParamUtil;
import com.schaex.util.PublicCloneable;

import java.lang.reflect.Array;

public final class ArrayUtil {
    private ArrayUtil() {}

    @SuppressWarnings("unchecked")
    public static <T extends PublicCloneable<T>> T[] cloneEntries(T[] array) {
        final T[] copy = (T[]) Array.newInstance(array.getClass().componentType(), array.length);

        try {
            for (int i = 0; i < copy.length; i++) {
                copy[i] = array[i].clone();
            }
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

        return copy;
    }

    public static <T> T getIfInRange(T[] array, int index) {
        return ParamUtil.isInRange(index, 0, array.length) ? array[index] : null;
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
