package com.schaex.days;

import com.schaex.arrays.ArrayUtil;
import com.schaex.util.FileUtil;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Day05 {
    public static void main(String... args) throws IOException {
        final Map<Integer, List<Integer>> allPrevious = new HashMap<>();

        final int[][] updates = FileUtil.transformFileContent(5, stream -> {
            final List<int[]> updatesList = new ArrayList<>();

            final AtomicBoolean foundAllRules = new AtomicBoolean();

            stream.forEach(line -> {
                if (!foundAllRules.get()) {
                    if (line.isEmpty()) {
                        foundAllRules.set(true);
                        return;
                    }

                    final int[] rule = ArrayUtil.intArrayFromStrings(line.split("\\|"));

                    allPrevious.computeIfAbsent(rule[1], i -> new ArrayList<>(16))
                            .add(rule[0]);
                } else {
                    updatesList.add(ArrayUtil.intArrayFromStrings(line.split(",")));
                }
            });

            return updatesList.toArray(int[][]::new);
        });

        int count = 0;
        int newlyCorrectCount = 0;

        for (int[] update : updates) {
            boolean correct = true;

            for (int i = 0; i < update.length - 1; i++) {
                final int current = update[i];
                final List<Integer> previous = allPrevious.get(current);

                for (int j = i + 1; j < update.length; j++) {
                    final int nextToTest = update[j];

                    if (previous.contains(nextToTest)) {
                        correct = false;

                        System.arraycopy(update, i, update, i + 1, j - i);
                        update[i] = nextToTest;
                        i--;

                        break;
                    }
                }
            }

            final int addend = update[update.length / 2];

            if (correct) {
                count += addend;
            } else {
                newlyCorrectCount += addend;
            }
        }

        // 4135
        System.out.println("Part one: " + count);

        // 5285
        System.out.println("Part two: " + newlyCorrectCount);
    }
}
