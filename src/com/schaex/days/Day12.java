package com.schaex.days;

import com.schaex.util.FileUtil;
import com.schaex.util.ParamUtil;
import com.schaex.util.UnsafeUtil;

import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;

public class Day12 {
    private static final char[][] MAP;
    private static final int HEIGHT;
    private static final int WIDTH;

    static {
        try {
            // Collect the input as char array, add padding in each direction
            final char[][] intermediateMap = FileUtil.transformFileLines(12, stream ->
                stream.map(String::toCharArray)
                        .map(array -> {
                            final char[] chars = new char[array.length + 2];
                            System.arraycopy(array, 0, chars, 1, array.length);

                            return chars;
                        }).toArray(char[][]::new));

            MAP = new char[intermediateMap.length + 2][intermediateMap[0].length];
            System.arraycopy(intermediateMap, 0, MAP, 1, intermediateMap.length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        HEIGHT = MAP.length;
        WIDTH = MAP[0].length;
    }

    public static void main(String... args) throws Exception {
        // This map will be used in both tasks
        final Map<Character, List<Region>> allRegions = new HashMap<>();

        System.out.print("Part one: ");

        // 1370100
        {
            for (int y = 0; y < HEIGHT; y++) {
                final char[] line = MAP[y];

                for (int x = 0; x < WIDTH; x++) {
                    final char type = line[x];

                    // The tile has already been marked as consumed
                    if (type == '\0') {
                        continue;
                    }

                    // Instantiate the region and fill it
                    final Region region = new Region();
                    computePointsInRegion(region, type, x, y);

                    // Mark all the tiles as consumed so that we can just skip them
                    for (Point point : region) {
                        MAP[point.y][point.x] = '\0';
                    }

                    allRegions.computeIfAbsent(type, key -> new ArrayList<>())
                            .add(region);
                }
            }

            int sum = 0;

            for (List<Region> regions : allRegions.values()) {
                for (Region region : regions) {
                    sum += region.area() * region.perimeter();
                }
            }

            System.out.println(sum);
        }

        System.out.print("Part two: ");

        // 818286
        {
            int sum = 0;

            for (Map.Entry<Character, List<Region>> entry : allRegions.entrySet()) {
                final char type = entry.getKey();

                for (Region region : entry.getValue()) {
                    final Map<Point, Character> markers = makeSideMarkers(type, region);

                    sum += region.area() * evaluateNumberOfSides(type, markers);

                    clearMap();
                }
            }

            System.out.println(sum);
        }
    }

    // Magic constants that will be used for bitwise operations
    private static final char NONE  = 0;
    private static final char LEFT  = 1;
    private static final char RIGHT = 2;
    private static final char UP    = 4;
    private static final char DOWN  = 8;

    // Array for convenience
    private static final char[] MASKS = {LEFT, RIGHT, UP, DOWN};

    private static Map<Point, Character> makeSideMarkers(char type, List<Point> points) {
        final Map<Point, Character> markers = new HashMap<>();

        // For convenience. Marks tiles in the array and populates the map
        final BiConsumer<Point, Character> effector = (point, mask) -> {
            MAP[point.y][point.x] |= mask;
            markers.compute(point, (key, oldValue) -> oldValue == null ? mask : (char) (oldValue | mask));
        };

        // Marks the region
        for (Point point : points) {
            MAP[point.y][point.x] = type;
        }

        for (Point point : points) {
            // Is this tile completely surrounded with other similar tiles?
            if (point.similarNeighbors == 4) {
                continue;
            }

            final int x = point.x, y = point.y;

            // Left
            if (x > 0 && MAP[y][x - 1] != type) {
                final Point markerPoint = new Point(x - 1, y);
                effector.accept(markerPoint, LEFT);
            }

            // Right
            if (x < WIDTH - 1 && MAP[y][x + 1] != type) {
                final Point markerPoint = new Point(x + 1, y);
                effector.accept(markerPoint, RIGHT);
            }

            // Up
            if (y > 0 && MAP[y - 1][x] != type) {
                final Point markerPoint = new Point(x, y - 1);
                effector.accept(markerPoint, UP);
            }

            // Down
            if (y < HEIGHT - 1 && MAP[y + 1][x] != type) {
                final Point markerPoint = new Point(x, y + 1);
                effector.accept(markerPoint, DOWN);
            }
        }

        return markers;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    private static int evaluateNumberOfSides(char type, Map<Point, Character> markers) {
        int count = 0;

        // Cache the entry set
        final Set<Map.Entry<Point, Character>> entries = markers.entrySet();

        // While this variable "has next"
        for (Iterator<Map.Entry<Point, Character>> it = entries.iterator(); it.hasNext(); ) {
            final Map.Entry<Point, Character> entry = it.next();

            // Stored value
            final char mask = entry.getValue();

            for (char simpleMask : MASKS) {
                // Is the mask composed of this simpleMask?
                if ((mask & simpleMask) == simpleMask) {
                    int dx, dy;

                    switch (simpleMask) {
                        case LEFT, RIGHT -> {
                            // Go down
                            dx = 0;
                            dy = 1;
                        }
                        case UP, DOWN -> {
                            // Go right
                            dx = 1;
                            dy = 0;
                        }
                        default -> {
                            // This should not happen
                            continue;
                        }
                    }

                    // Mask to remove this component from a tile
                    final char antiMask = (char) ~simpleMask;
                    final Point point = entry.getKey();

                    int currentX = point.x, currentY = point.y;
                    char currentTile;

                    // Move to a corner.
                    while (ParamUtil.isInRange(currentX += dx, 0, WIDTH) &&
                            ParamUtil.isInRange(currentY += dy, 0, HEIGHT) &&
                            (currentTile = MAP[currentY][currentX]) != type &&
                            (currentTile & simpleMask) == simpleMask);

                    // Turn around
                    dx = -dx; dy = -dy;

                    // Now remove this component in this line
                    while (ParamUtil.isInRange(currentX += dx, 0, WIDTH) &&
                            ParamUtil.isInRange(currentY += dy, 0, HEIGHT) &&
                            (currentTile = MAP[currentY][currentX]) != type &&
                            (currentTile & simpleMask) == simpleMask) {
                        // We can operate on the map this way because
                        // A) We override the equals() method inside the Point class
                        // B) We won't use this iterator anymore
                        final Point currentPoint = new Point(currentX, currentY);

                        // Is this tile only a single component?
                        if (currentTile == simpleMask) {
                            markers.remove(currentPoint);
                            MAP[currentY][currentX] = NONE;
                        } else {
                            markers.put(currentPoint, MAP[currentY][currentX] &= antiMask);
                        }
                    }

                    // We now cleared an entire side
                    count++;
                }
            }

            // Get a new iterator so that the changes take effect
            it = entries.iterator();
        }

        return count;
    }

    // Sets all chars to the null character
    private static void clearMap() {
        for (char[] chars : MAP) {
            UnsafeUtil.clearArray(chars);
        }
    }

    private static void computePointsInRegion(Region region, char type, int x, int y) {
        // If this point has already been added, don't need to check
        if (region.contains(x, y)) {
            return;
        }

        // Instantiate and add a new point
        final Point point = new Point(x, y);
        region.add(point);

        // Left
        if (x > 0 && MAP[y][x - 1] == type) {
            point.similarNeighbors++;
            computePointsInRegion(region, type, x - 1, y);
        }

        // Right
        if (x < WIDTH - 1 && MAP[y][x + 1] == type) {
            point.similarNeighbors++;
            computePointsInRegion(region, type, x + 1, y);
        }

        // Up
        if (y > 0 && MAP[y - 1][x] == type) {
            point.similarNeighbors++;
            computePointsInRegion(region, type, x, y - 1);
        }

        // Down
        if (y < HEIGHT - 1 && MAP[y + 1][x] == type) {
            point.similarNeighbors++;
            computePointsInRegion(region, type, x, y + 1);
        }
    }

    private static class Point extends com.schaex.frequently_used.Point {
        int similarNeighbors;

        Point(int x, int y) {
            super(x, y);
        }

        // For debugging
        @Override
        public String toString() {
            return super.toString() + " -> " + similarNeighbors;
        }
    }

    private static class Region extends ArrayList<Point> {
        boolean contains(int x, int y) {
            for (Point other : this) {
                if (other.x == x && other.y == y) {
                    return true;
                }
            }

            return false;
        }

        int area() {
            return size();
        }

        int perimeter() {
            int result = 0;

            for (Point point : this) {
                result += (4 - point.similarNeighbors);
            }

            return result;
        }
    }
}
