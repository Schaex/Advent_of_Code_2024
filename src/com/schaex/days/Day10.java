package com.schaex.days;

import com.schaex.util.FileUtil;
import com.schaex.util.ParamUtil;
import com.schaex.util.tuples.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Day10 {
    private static final char[][] MAP;
    private static final int HEIGHT;
    private static final int WIDTH;

    private static final List<Point> STARTS = new ArrayList<>();
    private static final List<Point> ENDS = new ArrayList<>();


    static {
        try {
            MAP = FileUtil.transformFileLines(10, stream -> {
                // AtomicInteger for its reference
                final AtomicInteger yHolder = new AtomicInteger();

                return stream.map(line -> {
                    final char[] chars = line.toCharArray();

                    // 0, 1, 2, 3,...
                    final int y = yHolder.getAndIncrement();

                    for (int x = 0; x < chars.length; x++) {
                        final char c = chars[x];

                        // Find start and end points for more efficiency
                        if (c == '0') {
                            STARTS.add(new Point(x, y));
                        } else if (c == '9') {
                            ENDS.add(new Point(x, y));
                        }
                    }

                    return chars;
                }).toArray(char[][]::new);
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Cache dimensions
        HEIGHT = MAP.length;
        WIDTH = MAP[0].length;
    }

    public static void main(String... args) {
        int countPart1 = 0; // 737
        int countPart2 = 0; // 1619

        int intermediary;

        for (Point start : STARTS) {
            for (Point end : ENDS) {
                // We don't need to check if the points are too far away from each other
                if (start.taxicabDistance(end) > 9) {
                    continue;
                }

                if ((intermediary = start.numberOfTrailsTo(end)) > 0) {
                    countPart1++;
                    countPart2 += intermediary;
                }
            }
        }

        System.out.println("Part one: " + countPart1);
        System.out.println("Part two: " + countPart2);
    }

    // Container for 2D-coordinates
    private record Point(int x, int y) {
        int taxicabDistance(int otherX, int otherY) {
            return Math.abs(x - otherX) + Math.abs(y - otherY);
        }

        int taxicabDistance(Point other) {
            return Math.abs(x - other.x) + Math.abs(y - other.y);
        }

        int numberOfTrailsTo(Point other) {
            return other.numberOfTrails(x, y, '1');
        }

        int numberOfTrails(int currentX, int currentY, int nextHeight) {
            int count = 0;

            int nextX, nextY;

            for (Direction direction : Direction.values()) {
                // If we are in bounds of the map and the next coordinates are not too far away from the goal
                if (ParamUtil.isInRange(nextX = currentX + direction.dx, 0, WIDTH)  &&
                        ParamUtil.isInRange(nextY = currentY + direction.dy, 0, HEIGHT) &&
                        taxicabDistance(nextX, nextY) <= ('9' - nextHeight)) {

                    // Is this the correct height?
                    if (MAP[nextY][nextX] != nextHeight) {
                        continue;
                    }

                    // At the end -> Add 1 to the counter of the previous call (from nextHeight = '8')
                    if (nextHeight == '9') {
                        return 1;
                    }

                    // Accumulate all recursively returned values
                    count += numberOfTrails(nextX, nextY, nextHeight + 1);
                }
            }

            return count;
        }

        // For debugging
        @Override
        public String toString() {
            return "[" + x + "," + y + "]";
        }
    }

    // Convenience enum, stolen from day 6 so that I don't need to write as much
    private enum Direction {
        UP(0, -1),
        RIGHT(1, 0),
        DOWN(0, 1),
        LEFT(-1,0);

        final int dx;
        final int dy;

        Direction(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }
    }
}
