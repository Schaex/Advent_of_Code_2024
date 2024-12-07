package com.schaex.days;

import com.schaex.arrays.ArrayUtil;
import com.schaex.util.FileUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BinaryOperator;

public class Day01 {
    public static void main(String... args) throws IOException {
        // Get columns as arrays
        int[][] table = FileUtil.getIntTableFromFile(1, " {3}");

        // Transpose so that they can be sorted
        table = ArrayUtil.transposeRectangular(table);

        final int[] leftList = table[0];
        final int[] rightList = table[1];

        // Cache length
        final int length = leftList.length;

        // Sort ascending
        Arrays.sort(leftList);
        Arrays.sort(rightList);

        System.out.print("Part one: ");

        // 1722302
        {
            int count = 0;

            for (int i = 0; i < length; i++) {
                int diff = leftList[i] - rightList[i];

                // Add absolute value
                count += (diff < 0) ? -diff : diff;
            }

            System.out.println(count);
        }

        System.out.print("Part two: ");

        // 20373490
        {
            // Maps to hold mappings "number -> count" of left and right column
            final Map<Integer, Integer> counterLeft = new HashMap<>();
            final Map<Integer, Integer> counterRight = new HashMap<>();

            // Function that is invoked to add a new mapping if the mapping does not exist (value == null)
            // or that increments the value by 1 if it already exists
            final BinaryOperator<Integer> mappingFunction = (key, value) -> value == null ? 1 : value + 1;

            for (int i = 0; i < length; i++) {
                counterLeft.compute(leftList[i], mappingFunction);
                counterRight.compute(rightList[i], mappingFunction);
            }

            int count = 0;

            // Only need to iterate over one entry set and get the value from the other map
            for (Map.Entry<Integer, Integer> entry : counterLeft.entrySet()) {
                final int key = entry.getKey();

                // Returns zero if mapping does not exist
                final int valueRight = counterRight.getOrDefault(key, 0);

                // Premature continue because the product would evaluate to zero anyway
                if (valueRight == 0) {
                    continue;
                }

                final int valueLeft = entry.getValue();

                count += (key * valueLeft * valueRight);
            }

            System.out.println(count);
        }
    }
}
