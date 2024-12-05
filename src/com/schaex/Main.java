package com.schaex;

import java.util.Arrays;
import java.util.stream.IntStream;

public class Main {
    public static void main(String... args) {
        final int[] array = IntStream.range(0, 20).toArray();

        System.out.println(Arrays.toString(array));

        System.arraycopy(array, 2, array, 2 + 1, 8);

        array[2] = 10;

        System.out.println(Arrays.toString(array));
    }
}
