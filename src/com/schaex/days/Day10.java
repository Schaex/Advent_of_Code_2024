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
        // Collect pairs that actually lead to each other
        final List<Pair<Point, Point>> startEndPairs = new ArrayList<>();

        System.out.print("Part one: ");

        // 737
        {
            for (Point start : STARTS) {
                for (Point end : ENDS) {
                    // We don't need to check if the points are too far away from each other
                    if (start.taxicabDistance(end) > 9) {
                        continue;
                    }

                    if (start.canReach(end)) {
                        startEndPairs.add(new Pair<>(start, end));
                    }
                }
            }

            System.out.println(startEndPairs.size());
        }

        System.out.print("Part two: ");

        // 1619
        {
            int count = 0;

            // We can now simply iterate over the list we built earlier
            for (Pair<Point, Point> pair : startEndPairs) {
                count += pair.left().numberOfTrailsTo(pair.right());
            }

            System.out.println(count);
        }
    }

    // Container for 2D-coordinates
    private record Point(int x, int y) {
        int taxicabDistance(int otherX, int otherY) {
            return Math.abs(x - otherX) + Math.abs(y - otherY);
        }

        int taxicabDistance(Point other) {
            return Math.abs(x - other.x) + Math.abs(y - other.y);
        }

        boolean canReach(Point other) {
            return other.canBeReached(x, y, '1');
        }

        boolean canBeReached(int currentX, int currentY, int nextHeight) {
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

                    // Did we reach the end?
                    if (nextHeight == '9') {
                        return true;
                    }

                    // Recursive call with the new coordinates and the next height
                    if (canBeReached(nextX, nextY, nextHeight + 1)) {
                        return true;
                    }
                }
            }

            // We checked all directions but none passed all checks
            return false;
        }

        int numberOfTrailsTo(Point other) {
            return other.numberOfTrails(x, y, '1');
        }

        // This method is very similar to canBeReached(). However, it counts all the paths
        int numberOfTrails(int currentX, int currentY, int nextHeight) {
            int count = 0;

            int nextX, nextY;

            for (Direction direction : Direction.values()) {
                if (ParamUtil.isInRange(nextX = currentX + direction.dx, 0, WIDTH)  &&
                        ParamUtil.isInRange(nextY = currentY + direction.dy, 0, HEIGHT) &&
                        taxicabDistance(nextX, nextY) <= ('9' - nextHeight)) {

                    if (MAP[nextY][nextX] != nextHeight) {
                        continue;
                    }

                    // Add 1 to the counter of the previous call (from nextHeight = '8')
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
