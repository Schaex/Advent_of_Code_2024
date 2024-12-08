package com.schaex.days;

import com.schaex.util.FileUtil;
import com.schaex.util.ParamUtil;

import java.io.IOException;
import java.util.*;

public class Day08 {
    public static void main(String... args) throws IOException {
        // Get the input as char array
        final char[][] map = FileUtil.transformFileContent(8,
                stream -> stream.map(String::toCharArray)
                        .toArray(char[][]::new));

        // Cache these important values. This is possible because the array is rectangular
        final int height = map.length;
        final int width = map[0].length;

        final Map<Character, List<Point>> frequencies = new HashMap<>();

        // Run through the input and collect the coordinates of the antennas
        for (int y = 0; y < height; y++) {
            final char[] line = map[y];

            for (int x = 0; x < width; x++) {
                final char frequency = line[x];

                if (frequency != '.') {
                    // Create the mapping if it doesn't exist and return either way
                    frequencies.computeIfAbsent(frequency, i -> new ArrayList<>())
                            .add(new Point(x, y));
                }
            }
        }

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
                    final int x1 = point1.x, y1 = point1.y;

                    for (Point point2 : points) {
                        // Again, a single point does not form a line
                        if (point1 == point2) {
                            continue;
                        }

                        final int x2 = point2.x, y2 = point2.y;

                        // Create a vector P1->P2
                        final int dx = x2 - x1, dy = y2 - y1;

                        Point point;

                        // Subtract the vector from P1
                        if ((point = Point.make(x1 - dx, y1 - dy, width, height)) != null) {
                            uniqueAntiNodes.add(point);
                        }

                        // Add the vector to P2
                        if ((point = Point.make(x2 + dx, y2 + dy, width, height)) != null) {
                            uniqueAntiNodes.add(point);
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

                        // Moved the coordinates inside this loop and made them not final
                        // so that we can extend them by the vector over and over again
                        int x1 = point1.x, y1 = point1.y;
                        int x2 = point2.x, y2 = point2.y;
                        final int dx = x2 - x1, dy = y2 - y1;

                        Point point;

                        // Turned the if-statements into while-loops
                        while ((point = Point.make(x1 -= dx, y1 -= dy, width, height)) != null) {
                            uniqueAntiNodes.add(point);
                        }

                        while ((point = Point.make(x2 += dx, y2 += dy, width, height)) != null) {
                            uniqueAntiNodes.add(point);
                        }
                    }
                }
            }

            System.out.println(uniqueAntiNodes.size());
        }
    }

    // Container for convenience
    private record Point(int x, int y) {
        // Static factory method to check whether the coordinates are in bounds
        static Point make(int x, int y, int width, int height) {
            if (ParamUtil.isInRange(x, 0, width) && ParamUtil.isInRange(y, 0, height)) {
                return new Point(x, y);
            }

            return null;
        }

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
