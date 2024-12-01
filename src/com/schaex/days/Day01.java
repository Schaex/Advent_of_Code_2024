package com.schaex.days;

import com.schaex.arrays.ArrayUtil;
import com.schaex.arrays.ParallelArray;
import com.schaex.util.FileUtil;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BinaryOperator;

public class Day01 {
    public static void main(String... args) throws Exception {
        final File file = DaysUtil.resource("Day_01.txt");
        Integer[][] table = FileUtil.getLinesFromFile(file)
                .stream()
                .map(s -> s.split(" {3}"))
                .map(pair -> new Integer[]{Integer.parseInt(pair[0]), Integer.parseInt(pair[1])})
                .toArray(Integer[][]::new);

        table = ArrayUtil.transposeRectangular(table);

        final int length = table[0].length;

        Arrays.sort(table[0]);
        Arrays.sort(table[1]);

        System.out.print("Part one: ");

        // 1722302
        {
            int count = 0;

            for (int i = 0; i < length; i++) {
                int diff = table[0][i] - table[1][i];

                if (diff < 0) {
                    diff = -diff;
                }

                count += diff;
            }

            System.out.println(count);
        }

        System.out.print("Part two: ");

        // 20373490
        {
            final Map<Integer, Integer> counterLeft = new HashMap<>();
            final Map<Integer, Integer> counterRight = new HashMap<>();

            final BinaryOperator<Integer> mappingFunction = (key, value) -> value == null ? 1 : value + 1;

            for (ParallelArray<Integer>.Slice slice : new ParallelArray<>(table[0], table[1])) {
                counterLeft.compute(slice.get(0), mappingFunction);
                counterRight.compute(slice.get(1), mappingFunction);
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
