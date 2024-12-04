package com.schaex.days;

import com.schaex.util.FileUtil;

import java.io.File;
import java.io.IOException;

public class Day02 {
    public static void main(String... args) throws IOException {
        final File file = DaysUtil.resource(2);

        final int[][] list = FileUtil.getIntTableFromFile(file, " ");

        System.out.print("Part one: ");

        // 421
        {
            int count = 0;

            for (int[] ints : list) {
                boolean safe = true;

                if (ints[0] < ints[1]) {
                    for (int i = 0; i < ints.length - 1; i++) {
                        final int diff = ints[i + 1] - ints[i];

                        if (diff < 1 || diff > 3) {
                            safe = false;
                            break;
                        }
                    }
                } else {
                    for (int i = 0; i < ints.length - 1; i++) {
                        final int diff = ints[i] - ints[i + 1];

                        if (diff < 1 || diff > 3) {
                            safe = false;
                            break;
                        }
                    }
                }

                if (safe) {
                    count++;
                }
            }

            System.out.println(count);
        }

        System.out.print("Part two: ");

        // 476
        {
            int count = 0;

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

    private static boolean testForSafetyAfterDampener(int[] ints, int indexToIgnore, boolean increasing) {
        final int diffFactor = increasing ? 1 : -1;
        final int maxIndex = indexToIgnore < ints.length - 1 ? ints.length - 1 : ints.length - 2;

        for (int i = 0; i < maxIndex; i++) {
            if (i == indexToIgnore) {
                continue;
            }

            final int first = ints[i];
            final int second = (i + 1 == indexToIgnore) ? ints[i + 2] : ints[i + 1];

            final int diff = (second - first) * diffFactor;

            if (diff < 1 || diff > 3) {
                return false;
            }
        }

        return true;
    }
}
