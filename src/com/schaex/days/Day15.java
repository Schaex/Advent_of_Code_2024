package com.schaex.days;

import com.schaex.util.FileUtil;
import com.schaex.util.tuples.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Day15 {
    // Important constants for parsing
    private static final char WALL  = '#';
    private static final char ROBOT = '@';
    private static final char BOX   = 'O';
    private static final char TILE  = '.';

    private static final char BOX_L = '[';
    private static final char BOX_R = ']';

    private static final int LEFT  = '<';
    private static final int RIGHT = '>';
    private static final int UP    = '^';
    private static final int DOWN  = 'v';

    private static final int VERTICAL_COST   = 100;
    private static final int HORIZONTAL_COST =   1;

    public static void main(String... args) throws IOException {
        final Day15Impl part1 = new Day15Impl();
        final Day15Impl part2 = new Day15Impl(part1);

        part1.part1();
        part2.part2();
    }

    // The tasks are entirely delegated to instances of this class
    private static class Day15Impl {
        final char[][] map;
        final int height;
        final int width;

        final char[] directions;

        int robotX, robotY;

        // Initial constructor that reads the input file
        Day15Impl() throws IOException {
            final Pair<char[][], char[]> input = FileUtil.transformFileLines(15, stream -> {
                // Transform the stream into an iterator for more complex behavior
                final Iterator<String> lines = stream.iterator();

                final List<char[]> map = new ArrayList<>();

                // Repeat until a blank line has been reached
                while (true) {
                    final String nextLine = lines.next();

                    if (nextLine.isEmpty()) {
                        break;
                    }

                    map.add(nextLine.toCharArray());
                }

                final StringBuilder builder = new StringBuilder();

                // Collect the string
                while (lines.hasNext()) {
                    builder.append(lines.next());
                }

                // Transform into the desired types. Return as tuple
                return new Pair<>(map.toArray(char[][]::new), builder.toString().toCharArray());
            });

            map = input.left();
            height = map.length;
            width = map[0].length;

            directions = input.right();

            // Find the robot
            findingLoop:
            for (robotY = 1; robotY < height; robotY++) {
                final char[] line = map[robotY];

                for (robotX = 1; robotX < width - 1; robotX++) {
                    if (line[robotX] == ROBOT) {
                        break findingLoop;
                    }
                }
            }
        }

        // "Copy" constructor that transforms the map into the double-wide map for part 2
        Day15Impl(Day15Impl part1) {
            height = part1.height;
            width = part1.width * 2;
            map = new char[height][width];
            directions = part1.directions;

            for (int y = 0; y < part1.height; y++) {
                for (int x = 0, hereX = 0; x < part1.width; x++) {
                    final char current = part1.map[y][x];

                    switch (current) {
                        // Walls and tiles are simply duplicated
                        case WALL, TILE -> map[y][hereX++] = map[y][hereX++] = current;
                        // The robot is still only one tile wide
                        case ROBOT -> {
                            robotX = hereX;
                            robotY = y;
                            map[y][hereX++] = ROBOT;
                            map[y][hereX++] = TILE;
                        }
                        // Boxes are now two tiles wide
                        case BOX -> {
                            map[y][hereX++] = BOX_L;
                            map[y][hereX++] = BOX_R;
                        }
                    }
                }
            }
        }

        void part1() {
            int dx, dy;

            for (char dir : directions) {
                switch (dir) {
                    case LEFT -> {
                        dx = -1;
                        dy = 0;
                    }
                    case RIGHT -> {
                        dx = 1;
                        dy = 0;
                    }
                    case UP -> {
                        dx = 0;
                        dy = -1;
                    }
                    case DOWN -> {
                        dx = 0;
                        dy = 1;
                    }
                    // Default branch for safety due to fast failure in case of an error
                    default -> throw new IllegalStateException("Illegal direction: '" + dir + "'");
                }

                // If this move was successful, update the robot's coordinates
                if (tryMovePart1(robotX, robotY, dx, dy)) {
                    robotX += dx;
                    robotY += dy;
                }
            }

            System.out.println("Part one: " + calculateGPS(BOX));
        }

        boolean tryMovePart1(int fromX, int fromY, int dx, int dy) {
            final int nextX = fromX + dx;
            final int nextY = fromY + dy;

            final char next = map[nextY][nextX];

            // Walls are always a dead end
            if (next == WALL) {
                return false;
            }

            // Try moving the box first
            if (next == BOX && !tryMovePart1(nextX, nextY, dx, dy)) {
                return false;
            }

            // We can safely move this tile now
            final char current = map[fromY][fromX];
            map[nextY][nextX] = current;
            map[fromY][fromX] = TILE;

            return true;
        }

        // Essentially identical to part1() except of the call to tryMovePart2() rather than tryMovePart1()
        void part2() {
            int dx, dy;

            for (char dir : directions) {
                switch (dir) {
                    case LEFT -> {
                        dx = -1;
                        dy = 0;
                    }
                    case RIGHT -> {
                        dx = 1;
                        dy = 0;
                    }
                    case UP -> {
                        dx = 0;
                        dy = -1;
                    }
                    case DOWN -> {
                        dx = 0;
                        dy = 1;
                    }
                    default -> throw new IllegalStateException("Illegal direction: '" + dir + "'");
                }

                if (tryMovePart2(robotX, robotY, dx, dy)) {
                    robotX += dx;
                    robotY += dy;
                }
            }

            System.out.println("Part two: " + calculateGPS(BOX_L));
        }

        boolean tryMovePart2(int fromX, int fromY, int dx, int dy) {
            final int nextX = fromX + dx, nextY = fromY + dy;
            final char next = map[nextY][nextX];

            if (next == WALL) {
                return false;
            }

            if (next == BOX_L || next == BOX_R) {
                // When moving horizontally (dy = 0) we don't need to check for the side of the box we want to push
                if (dy == 0) {
                    if (!tryMovePart2(nextX, nextY, dx, dy)) {
                        return false;
                    }
                } else {
                    // Target the left half of the box
                    final int boxLX = next == BOX_L ? nextX : nextX - 1;

                    // Move this and all the other boxes in the way only if possible
                    if (canMoveTwoWideBoxUpOrDown(boxLX, nextY, dy)) {
                        doMoveTwoWideBoxUpOrDown(boxLX, nextY, dy);
                    } else {
                        return false;
                    }
                }
            }

            // We checked all cases and can move safely now
            final char current = map[fromY][fromX];
            map[nextY][nextX] = current;
            map[fromY][fromX] = TILE;

            return true;
        }

        boolean canMoveTwoWideBoxUpOrDown(int boxLX, int boxLY, int dy) {
            final int nextY = boxLY + dy;

            final char nextLeft = map[nextY][boxLX];
            final char nextRight = map[nextY][boxLX + 1];

            // Any walls in the way result in a fail
            if (nextLeft == WALL || nextRight == WALL) {
                return false;
            }

            // This box is perfectly aligned with another one
            if (nextLeft == BOX_L) {
                return canMoveTwoWideBoxUpOrDown(boxLX, nextY, dy);
            }

            // This box is not perfectly aligned with at least one other box

            boolean canMoveOtherBox_es = true;

            // Check left box
            if (nextLeft == BOX_R) {
                canMoveOtherBox_es &= canMoveTwoWideBoxUpOrDown(boxLX - 1, nextY, dy);
            }

            // Check right box
            if (nextRight == BOX_L) {
                canMoveOtherBox_es &= canMoveTwoWideBoxUpOrDown(boxLX + 1, nextY, dy);
            }

            return canMoveOtherBox_es;
        }

        // Very similar in structure to canMoveTwoWideBoxUpOrDown()
        // -> We don't need to perform any safety checks now because this method is only called right after it.
        void doMoveTwoWideBoxUpOrDown(int boxLX, int boxLY, int dy) {
            final int nextY = boxLY + dy;

            final char nextLeft = map[nextY][boxLX];
            final char nextRight = map[nextY][boxLX + 1];

            // Move other boxes that are in the way
            if (nextLeft == BOX_L) {
                doMoveTwoWideBoxUpOrDown(boxLX, nextY, dy);
            } else {
                if (nextLeft == BOX_R) {
                    doMoveTwoWideBoxUpOrDown(boxLX - 1, nextY, dy);
                }

                if (nextRight == BOX_L) {
                    doMoveTwoWideBoxUpOrDown(boxLX + 1, nextY, dy);
                }
            }

            // We can safely move both halfs of the box now
            map[nextY][boxLX] = BOX_L;
            map[nextY][boxLX + 1] = BOX_R;

            map[boxLY][boxLX] = TILE;
            map[boxLY][boxLX + 1] = TILE;
        }

        int calculateGPS(char boxMarker) {
            int count = 0;

            for (int y = 1; y < height - 1; y++) {
                for (int x = 1; x < width - 1; x++) {
                    if (map[y][x] == boxMarker) {
                        count += (HORIZONTAL_COST * x + VERTICAL_COST * y);
                    }
                }
            }

            return count;
        }

        @SuppressWarnings("unused")
        void printMap() {
            for (char[] line : map) {
                System.out.println(line);
            }
        }
    }
}
