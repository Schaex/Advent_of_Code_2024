package com.schaex.days;

import com.schaex.arrays.ArrayUtil;
import com.schaex.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BinaryOperator;

public class Day01 {
    public static void main(String... args) throws IOException {
        final File file = DaysUtil.resource("Day_01.txt");

        int[][] table = FileUtil.getIntTableFromFile(file, " {3}");
        table = ArrayUtil.transposeRectangular(table);

        final int[] leftList = table[0];
        final int[] rightList = table[1];

        final int length = leftList.length;

        Arrays.sort(leftList);
        Arrays.sort(rightList);

        System.out.print("Part one: ");

        // 1722302
        {
            int count = 0;

            for (int i = 0; i < length; i++) {
                int diff = leftList[i] - rightList[i];

                count += (diff < 0) ? -diff : diff;
            }

            System.out.println(count);
        }

        System.out.print("Part two: ");

        // 20373490
        {
            final Map<Integer, Integer> counterLeft = new HashMap<>();
            final Map<Integer, Integer> counterRight = new HashMap<>();

            final BinaryOperator<Integer> mappingFunction = (key, value) -> value == null ? 1 : value + 1;

            for (int i = 0; i < length; i++) {
                counterLeft.compute(leftList[i], mappingFunction);
                counterRight.compute(rightList[i], mappingFunction);
            }

            int count = 0;

            for (Map.Entry<Integer, Integer> entry : counterLeft.entrySet()) {
                final int key = entry.getKey();
                final int valueLeft = entry.getValue();
                final int valueRight = counterRight.getOrDefault(key, 0);

                count += (key * valueLeft * valueRight);
            }

            System.out.println(count);
        }
    }
}
