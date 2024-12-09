package com.schaex.days;

import com.schaex.util.FileUtil;
import com.schaex.util.ParamUtil;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class Day08 {
    public static void main(String... args) throws IOException {
        final Map<Character, List<Point>> frequencies = new HashMap<>();

        // Lazily get the coordinates of each antenna without the need to collect the lines of the input
        final int[] dimensions = FileUtil.transformFileLines(8, stream -> {
            // AtomicInteger so that we can use a reference inside a lambda expression
            final AtomicInteger currentY = new AtomicInteger();

            // Transform the stream to an iterator because we need to fetch the first element independently
            final Iterator<char[]> it = stream.map(String::toCharArray).iterator();

            // Get the first element and cache its length
            final char[] firstLine = it.next();
            final int width = firstLine.length;

            // This will run for each line
            final Consumer<char[]> collector = line -> {
                for (int x = 0; x < width; x++) {
                    final char frequency = line[x];

                    if (frequency != '.') {
                        // Create the mapping if it doesn't exist and return either way
                        frequencies.computeIfAbsent(frequency, i -> new ArrayList<>())
                                .add(new Point(x, currentY.get()));
                    }
                }

                // Equivalent to an atomic ++currentY
                currentY.incrementAndGet();
            };

            collector.accept(firstLine);
            it.forEachRemaining(collector);

            // Instantiate a small array that holds the width and the height of the map
            return new int[]{width, currentY.get()};
        });

        final int width = dimensions[0];
        final int height = dimensions[1];

        System.out.print("Part one: ");

        // 348
        {
            // Use a Set so only distinct points will be added (see the equals() method of the Point record)
            final Set<Point> uniqueAntiNodes = new HashSet<>();

            // Only iterate over the lists, we don't need to know the characters now
            for (List<Point> points : frequencies.values()) {
                // A single point does not form a line
                if (points.size() < 2) {
                    continue;
                }

                for (Point point1 : points) {
                    for (Point point2 : points) {
                        // Again, a single point does not form a line
                        if (point1 == point2) {
                            continue;
                        }

                        int x1 = point1.x, y1 = point1.y;
                        int x2 = point2.x, y2 = point2.y;

                        // Create a vector P1->P2
                        final int dx = x2 - x1, dy = y2 - y1;

                        // Subtract the vector from P1
                        if (ParamUtil.isInRange(x1 -= dx, 0, width) && ParamUtil.isInRange(y1 -= dy, 0, height)) {
                            uniqueAntiNodes.add(new Point(x1, y1));
                        }

                        // Add the vector to P2
                        if (ParamUtil.isInRange(x2 += dx, 0, width) && ParamUtil.isInRange(y2 += dy, 0, height)) {
                            uniqueAntiNodes.add(new Point(x2, y2));
                        }
                    }
                }
            }

            System.out.println(uniqueAntiNodes.size());
        }

        System.out.print("Part two: ");

        // 1221
        {
            // This is very similar to part 1, so I'll just annotate the differences
            final Set<Point> uniqueAntiNodes = new HashSet<>();

            for (List<Point> points : frequencies.values()) {
                if (points.size() < 2) {
                    continue;
                }

                for (Point point1 : points) {
                    // The antenna itself is an antinode because there are at least one other antenna with the same frequency
                    uniqueAntiNodes.add(point1);

                    for (Point point2 : points) {
                        if (point1 == point2) {
                            continue;
                        }

                        int x1 = point1.x, y1 = point1.y;
                        int x2 = point2.x, y2 = point2.y;
                        final int dx = x2 - x1, dy = y2 - y1;

                        // Turned the if-statements into while-loops
                        while (ParamUtil.isInRange(x1 -= dx, 0, width) && ParamUtil.isInRange(y1 -= dy, 0, height)) {
                            uniqueAntiNodes.add(new Point(x1, y1));
                        }

                        while (ParamUtil.isInRange(x2 += dx, 0, width) && ParamUtil.isInRange(y2 += dy, 0, height)) {
                            uniqueAntiNodes.add(new Point(x2, y2));
                        }
                    }
                }
            }

            System.out.println(uniqueAntiNodes.size());
        }
    }

    // Container for convenience
    private record Point(int x, int y) {
        // equals() implementation that is important for using Sets
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Point point)) return false;
            return x == point.x && y == point.y;
        }

        // hashCode() implementation that is important for hash-dependent collections, such as a HashSet
        @Override
        public int hashCode() {
            return (x << 24) + (y << 8);
        }
    }
}
