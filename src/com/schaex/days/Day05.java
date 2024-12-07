package com.schaex.days;

import com.schaex.arrays.ArrayUtil;
import com.schaex.util.FileUtil;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Day05 {
    public static void main(String... args) throws IOException {
        // Mapping "page number -> list of numbers that can only come before"
        final Map<Integer, List<Integer>> allPrevious = new HashMap<>();

        // Gets a list of all updates -> 1 update = 1 int array
        final int[][] updates = FileUtil.transformFileContent(5, stream -> {
            // Intermediary list to collect only the updates
            final List<int[]> updatesList = new ArrayList<>();

            // AtomicBoolean to be used inside the lambda
            final AtomicBoolean foundAllRules = new AtomicBoolean();

            stream.forEach(line -> {
                if (!foundAllRules.get()) {
                    if (line.isEmpty()) {
                        foundAllRules.set(true);
                        return;
                    }

                    final int[] rule = ArrayUtil.intArrayFromStrings(line.split("\\|"));

                    // Get the list and add. If there is none, instantiate a new one
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

        // Iterate to evaluate both tasks in one go
        for (int[] update : updates) {
            // Marker flag, see below
            boolean correct = true;

            // Don't need to check the last element as there can't be a wrong page number after that
            for (int i = 0; i < update.length - 1; i++) {
                final int current = update[i];
                final List<Integer> previous = allPrevious.get(current);

                // Check the following numbers (start = i + 1)
                for (int j = i + 1; j < update.length; j++) {
                    final int nextToTest = update[j];

                    if (previous.contains(nextToTest)) {
                        // If there is an incorrect page number, mark as incorrect
                        correct = false;

                        // Move elements one to the right, leaving a gap at the current index "i"
                        System.arraycopy(update, i, update, i + 1, j - i);

                        // Put wrong page number right before the moved block
                        update[i] = nextToTest;

                        // We need to check if the numbers inside the moved block AND after are correct
                        // Thus, the variable has to be decreased by 1, as it will be increased right after.
                        i--;

                        break;
                    }
                }
            }

            // Get the middle page number, regardless if the array had to be modified or not
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
