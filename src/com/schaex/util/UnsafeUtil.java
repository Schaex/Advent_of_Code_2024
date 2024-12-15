package com.schaex.util;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public final class UnsafeUtil {
    public static final Unsafe U;

    static {
        try {
            final Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);

            U = (Unsafe) unsafeField.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void fillArray(char[] array, byte value) {
        U.setMemory(array, Unsafe.ARRAY_CHAR_BASE_OFFSET, (long) array.length * Unsafe.ARRAY_CHAR_INDEX_SCALE, value);
    }

    public static void clearArray(char[] array) {
        fillArray(array, (byte) 0);
    }
}
