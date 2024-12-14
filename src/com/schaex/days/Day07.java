package com.schaex.days;

import com.schaex.arrays.ArrayUtil;
import com.schaex.util.FileUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class Day07 {
    public static void main(String... args) throws IOException {
        // Get lines in LinkedList => enable removal of entries
        final List<Line> lines = FileUtil.transformFileLines(7, stream ->
                stream.map(line -> line.split(": "))
                        .map(array -> new Line(array[0], array[1].split(" ")))
                        .collect(Collectors.toCollection(LinkedList::new)));

        // Initialize shared counter for both tasks
        long count = 0L;

        System.out.print("Part one: ");

        // 538191549061
        {
            final Iterator<Line> it = lines.iterator();

            while (it.hasNext()) {
                final Line line = it.next();

                // Cache important constants for this line
                final long value = line.value;
                final long[] numbers = line.numbers;
                final int lengthM1 = numbers.length - 1;

                // Calculate number of permutations
                final int cutoff = 1 << (lengthM1);

                operatorLoop:
                for (int operationSet = 0; operationSet < cutoff; operationSet++) {
                    // The first number is taken as-is
                    long calculated = numbers[0];

                    // If there are 'n' elements, we can only perform 'n - 1' operations
                    for (int i = 0; i < lengthM1; i++) {
                        final long next = numbers[i + 1];

                        // Get value of the 'i-th' bit
                        if (((operationSet >>> i) & 1) == Operators.ADD) {
                            calculated += next;
                        } else {
                            calculated *= next;
                        }

                        // Check whether we went above the target value or even had an overflow
                        if (calculated > value || calculated < 0) {
                            continue operatorLoop;
                        }
                    }

                    // We arrived at the target value
                    if (calculated == value) {
                        count += value;

                        // Remove this line from the list so that we don't have to evaluate it again in task 2
                        it.remove();

                        break;
                    }
                }
            }

            System.out.println(count);
        }

        System.out.print("Part two: ");

        // 34612812972206
        {
            final Operators operatorContainer = new Operators();
            int[] operators = operatorContainer.operators;

            // Iterate over the remaining elements
            for (Line line : lines) {
                // Set all elements in the array to zero (only important for subsequent loops but not the first one)
                operatorContainer.clear();

                // Again, cache important constants, this time including the string representations of each number
                final long value = line.value;
                final long[] numbers = line.numbers;
                final int lengthM1 = numbers.length - 1;

                // Number of permutations needs to be calculated first
                int cutoff = 1;

                // Calculates 3^lengthM1
                for (int i = 0; i < lengthM1; i++) {
                    cutoff *= 3;
                }

                operatorLoop: // Note the call to operatorContainer.increase()
                for (int i = 0; i < cutoff; i++, operatorContainer.increase()) {
                    // Again, the first number is taken as-is
                    long calculated = numbers[0];

                    // Iterate over each index of the "operators" array, limited by lengthM1
                    for (int index = 0; index < lengthM1; index++) {
                        final long next = numbers[index + 1];

                        // This value can only be 0, 1 or 2
                        // Omitted "case Operators.CONCATENATE" by using a default branch instead
                        switch (operators[index]) {
                            case Operators.ADD -> calculated += next;
                            case Operators.MULTIPLY -> calculated *= next;
                            default -> calculated = concatenate(calculated, next);
                        }

                        // Again, check for overshoot or even overflow
                        if (calculated > value || calculated < 0) {
                            continue operatorLoop;
                        }
                    }

                    // Check if the value matches this time
                    if (calculated == value) {
                        count += value;
                        break;
                    }
                }
            }

            System.out.println(count);
        }
    }

    // Cache the powers of 10 so that they don't need to be calculated over and over again
    private static final long[] POWERS_OF_TEN = LongStream.iterate(10L, value -> value > 0L, value -> value * 10L).toArray();

    private static long concatenate(long left, long right) {
        for (long power : POWERS_OF_TEN) {
            // Found the smallest power of 10 that is larger than the right number
            if (right < power) {
                // e.g. "123" + "456" -> 123 * 1000 + 456 = 123456
                return left * power + right;
            }
        }

        throw new IllegalArgumentException("This should not happen while concatenating " + left + " with " + right);
    }

    // Container for each line
    private record Line(long value, long[] numbers) {
        Line(String value, String[] splitNumbers) {
            this(Long.parseLong(value), ArrayUtil.longArrayFromStrings(splitNumbers));
        }
    }

    // Utility class that encapsulates the "trits" of a trinary number
    private static class Operators {
        static final int ADD = 0;
        static final int MULTIPLY = 1;
        static final int CONCATENATE = 2;

        final int[] operators = new int[32];

        // Increments by 1
        void increase() {
            increase(0);
        }

        // Increments at the specified index, carrying the 1 if necessary
        void increase(int index) {
            final int value = operators[index];

            if (value == 2) {
                operators[index] = 0;
                increase(index + 1);
            } else {
                operators[index] = value + 1;
            }
        }

        // Sets all trits to zero
        void clear() {
            Arrays.fill(operators, 0);
        }
    }
}
