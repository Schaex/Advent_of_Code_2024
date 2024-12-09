package com.schaex.days;

import com.schaex.arrays.ArrayUtil;
import com.schaex.util.FileUtil;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Day02 {
    public static void main(String... args) throws IOException {
        // Get reports as arrays inside LinkedList => enable removal of entries
        final List<int[]> list = FileUtil.transformFileLines(2,
                stream -> stream.map(line -> line.split(" "))
                        .map(ArrayUtil::intArrayFromStrings)
                        .collect(Collectors.toCollection(LinkedList::new)));

        int count = 0;

        System.out.print("Part one: ");

        // 421
        {
            final Iterator<int[]> it = list.iterator();

            loop:
            while (it.hasNext()) {
                final int[] ints = it.next();

                // Is increasing
                if (ints[0] < ints[1]) {
                    for (int i = 0; i < ints.length - 1; i++) {
                        final int diff = ints[i + 1] - ints[i];

                        if (diff < 1 || diff > 3) {
                            continue loop;
                        }
                    }
                } else {
                    for (int i = 0; i < ints.length - 1; i++) {
                        final int diff = ints[i] - ints[i + 1];

                        if (diff < 1 || diff > 3) {
                            continue loop;
                        }
                    }
                }

                // We checked each entry and never had to break out
                count++;

                // We won't need to check this one again later
                it.remove();
            }

            System.out.println(count);
        }

        System.out.print("Part two: ");

        // 476
        {
            for (int[] ints : list) {
                final int length = ints.length;

                for (int i = 0; i < length; i++) {
                    if (testForSafetyAfterDampener(ints, i, true) || testForSafetyAfterDampener(ints, i, false)) {
                        count++;
                        break;
                    }
                }
            }

            System.out.println(count);
        }
    }

    // Check each index with brute force!
    private static boolean testForSafetyAfterDampener(int[] ints, int indexToIgnore, boolean increasing) {
        final int diffFactor = increasing ? 1 : -1;

        // If indexToIgnore is equal to the largest index, only check until the previous one
        // This prevents an ArrayIndexOutOfBoundsException down the line
        final int maxIndex = (indexToIgnore == ints.length - 1) ? ints.length - 2 : ints.length - 1;

        for (int i = 0; i < maxIndex; i++) {
            // Explicitly ignore this index
            if (i == indexToIgnore) {
                continue;
            }

            final int first = ints[i];

            // If we ignore the next index, fetch the element that comes after that
            final int second = (i + 1 == indexToIgnore) ? ints[i + 2] : ints[i + 1];

            final int diff = (second - first) * diffFactor;

            // Premature return
            if (diff < 1 || diff > 3) {
                return false;
            }
        }

        return true;
    }
}
